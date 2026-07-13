package com.loglite.server.reactive;

import com.loglite.server.dto.LogIngestRequest;
import com.loglite.server.entity.LogEntry;
import com.loglite.server.ingest.LogBatchWriter;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.time.Instant;
import java.util.UUID;

/**
 * Exposes a non-blocking, backpressure-aware log ingestion route on Reactor Netty,
 * running alongside the main servlet-based application on a separate port.
 */
@Component
public class ReactiveIngestServer {

    private final LogBatchWriter batchWriter;
    private final int port;
    private DisposableServer server;

    public ReactiveIngestServer(LogBatchWriter batchWriter,
                                 @Value("${loglite.reactive.port:8081}") int port) {
        this.batchWriter = batchWriter;
        this.port = port;
    }

    @EventListener(ContextRefreshedEvent.class)
    public synchronized void start() {
        if (server != null) {
            return;
        }

        RouterFunction<ServerResponse> router = RouterFunctions.route()
                .POST("/reactive/logs", this::ingest)
                .build();

        HttpHandler httpHandler = RouterFunctions.toHttpHandler(router);
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

        server = HttpServer.create()
                .port(port)
                .handle(adapter)
                .bindNow();
    }

    private Mono<ServerResponse> ingest(org.springframework.web.reactive.function.server.ServerRequest request) {
        return request.bodyToMono(LogIngestRequest.class)
                .map(this::toEntry)
                .doOnNext(batchWriter::submit)
                .then(ServerResponse.accepted().build());
    }

    private LogEntry toEntry(LogIngestRequest requestBody) {
        return new LogEntry(
                UUID.randomUUID(),
                requestBody.timestamp() != null ? requestBody.timestamp() : Instant.now(),
                requestBody.loggerName(),
                requestBody.level(),
                requestBody.message(),
                requestBody.threadName(),
                requestBody.metadata());
    }

    @PreDestroy
    public synchronized void stop() {
        if (server != null) {
            server.disposeNow();
            server = null;
        }
    }
}
