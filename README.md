# Loglite

Lightweight, developer-first logging toolkit for Java. Loglite is made up of a small
set of focused components:

- **loglite-core** – a minimal, dependency-free logging API (loggers, appenders,
  formatters, MDC) that everything else builds on.
- **loglite-server** – a Spring Boot REST backend that ingests log events into
  Postgres and exposes query, export, stats, and SQL console endpoints.
- **loglite-cli** – a command-line client (`loglite-cli`) for querying, tailing,
  parsing, and uploading logs against a `loglite-server` instance.
- **loglite-slf4j** – an SLF4J 2.x binding that routes `org.slf4j.Logger` calls
  through `loglite-core`.
- **loglite-logback-appender** – a Logback appender that forwards events to
  `loglite-server` over HTTP.
- **loglite-spring-boot-starter** – auto-configures log forwarding for Spring Boot
  applications.
- **loglite-integrations** – lightweight adapters (servlet request logging filter,
  Spring Security MDC binding, HTTP client correlation/logging middleware).

## Running locally

Start Postgres with Docker Compose:

```bash
docker compose up -d
```

Build and run the server:

```bash
mvn -pl loglite-server -am spring-boot:run
```

The server listens on `http://localhost:8080` and requires an `X-API-Key` header
(default `changeme`, configurable via `loglite.security.api-key`) on all
`/api/v1/logs/**` routes.

## Using the CLI

Build the CLI:

```bash
mvn -pl loglite-cli -am package
```

Configure it once, then query:

```bash
java -jar loglite-cli/target/loglite-cli.jar config --url http://localhost:8080 --key changeme
java -jar loglite-cli/target/loglite-cli.jar query --level ERROR,WARN --from 1h
```

See [docs/cli-reference.md](docs/cli-reference.md) for the full command list, and
[docs/quickstart.md](docs/quickstart.md) for a step-by-step walkthrough of
configuring logging and streaming events into `loglite-server`.

## Modules

This is a multi-module Maven build; run `mvn -T1C verify` from the repository root
to build and test everything.
