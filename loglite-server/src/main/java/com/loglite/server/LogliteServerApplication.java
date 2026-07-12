package com.loglite.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Loglite REST ingestion and query server.
 */
@SpringBootApplication
public class LogliteServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogliteServerApplication.class, args);
    }
}
