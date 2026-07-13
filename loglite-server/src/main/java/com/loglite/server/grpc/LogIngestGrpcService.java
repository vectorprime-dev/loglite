package com.loglite.server.grpc;

import com.loglite.core.LogLevel;
import com.loglite.server.entity.LogEntry;
import com.loglite.server.ingest.LogBatchWriter;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.UUID;

/**
 * gRPC implementation of {@link LogIngestServiceGrpc.LogIngestServiceImplBase}, accepting
 * both single-entry and client-streaming high-throughput ingestion.
 */
@GrpcService
public class LogIngestGrpcService extends LogIngestServiceGrpc.LogIngestServiceImplBase {

    private final LogBatchWriter batchWriter;

    public LogIngestGrpcService(LogBatchWriter batchWriter) {
        this.batchWriter = batchWriter;
    }

    @Override
    public void ingest(LogEntryMessage request, StreamObserver<IngestResponse> responseObserver) {
        batchWriter.submit(toEntry(request));
        responseObserver.onNext(IngestResponse.newBuilder().setAccepted(1).build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<LogEntryMessage> ingestStream(StreamObserver<IngestResponse> responseObserver) {
        return new StreamObserver<>() {
            private int count = 0;

            @Override
            public void onNext(LogEntryMessage value) {
                batchWriter.submit(toEntry(value));
                count++;
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(IngestResponse.newBuilder().setAccepted(count).build());
                responseObserver.onCompleted();
            }
        };
    }

    private LogEntry toEntry(LogEntryMessage message) {
        Instant timestamp = message.getTimestamp().isBlank() ? Instant.now() : Instant.parse(message.getTimestamp());
        LogLevel level = message.getLevel().isBlank() ? LogLevel.INFO : LogLevel.valueOf(message.getLevel().toUpperCase());
        return new LogEntry(
                UUID.randomUUID(),
                timestamp,
                message.getLoggerName(),
                level,
                message.getMessage(),
                message.getThreadName(),
                message.getMetadataMap());
    }
}
