package com.loglite.core;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Wraps {@link ExecutorService} instances and tasks so that the submitting thread's
 * {@link MDC} context is copied onto the worker thread for the duration of the task,
 * then restored to whatever the worker thread held before.
 */
public final class ContextPropagatingExecutors {

    private ContextPropagatingExecutors() {
    }

    /**
     * @param delegate the executor service to wrap
     * @return an executor service that propagates the caller's MDC context to submitted tasks
     */
    public static ExecutorService wrap(ExecutorService delegate) {
        return new ContextPropagatingExecutorService(delegate);
    }

    /**
     * @param task the task to wrap
     * @return a runnable that installs a snapshot of the current thread's MDC context before
     *         running the task, and restores the previous context afterward
     */
    public static Runnable wrap(Runnable task) {
        Map<String, String> snapshot = MDC.getCopyOfContextMap();
        return () -> runWithContext(snapshot, task);
    }

    /**
     * @param task the task to wrap
     * @param <V>  the task's result type
     * @return a callable that installs a snapshot of the current thread's MDC context before
     *         running the task, and restores the previous context afterward
     */
    public static <V> Callable<V> wrap(Callable<V> task) {
        Map<String, String> snapshot = MDC.getCopyOfContextMap();
        return () -> callWithContext(snapshot, task);
    }

    private static void runWithContext(Map<String, String> snapshot, Runnable task) {
        Map<String, String> previous = MDC.getCopyOfContextMap();
        applyContext(snapshot);
        try {
            task.run();
        } finally {
            applyContext(previous);
        }
    }

    private static <V> V callWithContext(Map<String, String> snapshot, Callable<V> task) throws Exception {
        Map<String, String> previous = MDC.getCopyOfContextMap();
        applyContext(snapshot);
        try {
            return task.call();
        } finally {
            applyContext(previous);
        }
    }

    private static void applyContext(Map<String, String> context) {
        MDC.clear();
        context.forEach(MDC::put);
    }

    private static final class ContextPropagatingExecutorService extends java.util.concurrent.AbstractExecutorService {
        private final ExecutorService delegate;

        private ContextPropagatingExecutorService(ExecutorService delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(Runnable command) {
            delegate.execute(wrap(command));
        }

        @Override
        public void shutdown() {
            delegate.shutdown();
        }

        @Override
        public java.util.List<Runnable> shutdownNow() {
            return delegate.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return delegate.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return delegate.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, java.util.concurrent.TimeUnit unit) throws InterruptedException {
            return delegate.awaitTermination(timeout, unit);
        }
    }

    /**
     * @return a new virtual-thread-per-task executor service that propagates MDC context
     */
    public static ExecutorService newVirtualThreadPerTaskExecutor() {
        return wrap(Executors.newVirtualThreadPerTaskExecutor());
    }
}
