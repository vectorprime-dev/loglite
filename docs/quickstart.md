# Quickstart

This walks through running `loglite-server`, sending it logs from a Java
application via the SLF4J binding, and querying them back with `loglite-cli`.

## 1. Start the server

```bash
docker compose up -d          # starts Postgres
mvn -pl loglite-server -am spring-boot:run
```

The server runs on `http://localhost:8080`. All `/api/v1/logs/**` routes require
an `X-API-Key` header — the default key is `changeme` (set via
`loglite.security.api-key`).

## 2. Add logging to your application

Add the SLF4J binding (or the Spring Boot starter, which wires this up for you)
to your application's dependencies:

```xml
<dependency>
    <groupId>com.loglite</groupId>
    <artifactId>loglite-slf4j</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Then log as usual:

```java
private static final Logger log = LoggerFactory.getLogger(MyService.class);

log.info("Processing order {}", orderId);
```

To forward these events to `loglite-server` over HTTP, use the Logback appender
(or add `loglite-spring-boot-starter` and set `loglite.url` / `loglite.api-key`
in `application.properties` for zero-config forwarding in a Spring Boot app):

```xml
<appender name="LOGLITE" class="com.loglite.logback.LogliteHttpAppender">
    <url>http://localhost:8080</url>
    <apiKey>changeme</apiKey>
</appender>
```

## 3. Configure the CLI

```bash
java -jar loglite-cli/target/loglite-cli.jar config --url http://localhost:8080 --key changeme
```

Or run the interactive wizard:

```bash
java -jar loglite-cli/target/loglite-cli.jar setup
```

## 4. Query your logs

```bash
# Everything from the last hour
loglite-cli query --from 1h

# Only errors, highlighted matches, as JSON
loglite-cli query --level ERROR --search "timeout" --format json

# Live tail
loglite-cli query --tail
```

See [docs/cli-reference.md](cli-reference.md) for every command and flag.
