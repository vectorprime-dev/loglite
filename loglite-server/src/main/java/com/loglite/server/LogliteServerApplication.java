package com.loglite.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Loglite REST ingestion and query server.
 */
@SpringBootApplication
@EnableScheduling
public class LogliteServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogliteServerApplication.class, args);
    }
}
