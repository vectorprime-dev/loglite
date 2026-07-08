# Loglite Open Source Issues List

This document lists all 105 open source issues defined for the `loglite` Java 25 logging toolkit.

---

## Category 1: Core SDK (`loglite-core`) & Formatters

### Issue 1: Define LogLevel Enum
- **Difficulty**: Easy
- **Labels**: `core`, `good-first-issue`, `feature`
- **Description**: Implement a standard `LogLevel` enum in the `loglite-core` module representing severities for logging events.
- **Acceptance Criteria**:
  - Enums created: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `FATAL`.
  - Each enum has an associated integer value (e.g., TRACE=100, DEBUG=200, INFO=300, WARN=400, ERROR=500, FATAL=600) for numeric comparisons.
  - Implement a method `boolean isEnabled(LogLevel configuredLevel)` that compares current severity to active severity.

### Issue 2: Create LogEvent Record
- **Difficulty**: Easy
- **Labels**: `core`, `feature`
- **Description**: Create a Java `record` named `LogEvent` to model structured log details immutable across downstream threads.
- **Acceptance Criteria**:
  - Fields included: `Instant timestamp`, `String loggerName`, `LogLevel level`, `String message`, `String threadName`, `Throwable throwable`, `Map<String, String> mdc`.
  - Record must have builder support or standard constructor initialization.
  - Immutable with proper defensive copy of metadata map.

### Issue 3: Implement Core Logger Interface & Standard Logger
- **Difficulty**: Easy
- **Labels**: `core`, `feature`
- **Description**: Create a base `Logger` interface outlining logging capability, alongside its standard implementation `LogliteLogger`.
- **Acceptance Criteria**:
  - Interface exposes logging methods: `trace(msg)`, `debug(msg)`, `info(msg)`, `warn(msg)`, `error(msg)`, `fatal(msg)`.
  - Support parameterized messages with brackets (e.g. `logger.info("Hello {}", name)`).
  - Implementation delegates event building to context handlers.

### Issue 4: Create Basic ConsoleAppender
- **Difficulty**: Easy
- **Labels**: `core`, `good-first-issue`, `feature`
- **Description**: Create a basic `ConsoleAppender` responsible for writing formatted logs to standard outputs.
- **Acceptance Criteria**:
  - Logs of level `WARN` and below are printed to `System.out`.
  - Logs of level `ERROR` and `FATAL` are printed to `System.err`.
  - Appender accepts a formatter layout class.

### Issue 5: Implement TextFormatter Layout
- **Difficulty**: Easy
- **Labels**: `core`, `feature`
- **Description**: Write a `TextFormatter` implementing a default layout to output log events in text format.
- **Acceptance Criteria**:
  - Formats as: `[YYYY-MM-DD HH:mm:ss.SSS] [LEVEL] [Thread] LoggerName - Message`.
  - Properly formats empty logger names or exceptions if present.

### Issue 6: Implement JsonFormatter using Jackson
- **Difficulty**: Easy
- **Labels**: `core`, `feature`
- **Description**: Create a `JsonFormatter` which formats `LogEvent` fields into a structured JSON string.
- **Acceptance Criteria**:
  - Uses `Jackson` data-binding library.
  - Serializes timestamp to ISO-8601 string, log level, thread name, logger name, message, and MDC object.
  - Ignores null values in serialization to keep log lines compact.

### Issue 7: Create Custom PatternFormatter
- **Difficulty**: Medium
- **Labels**: `core`, `feature`
- **Description**: Write a parser-based `PatternFormatter` using format strings (e.g. `%d [%t] %p %c - %m%n`).
- **Acceptance Criteria**:
  - `%d` formats date-time.
  - `%t` formats thread name.
  - `%p` formats level.
  - `%c` formats logger category name.
  - `%m` formats message.
  - `%n` outputs system line separator.

### Issue 8: Create ThreadLocal-based MDC (Mapped Diagnostic Context)
- **Difficulty**: Easy
- **Labels**: `core`, `good-first-issue`, `feature`
- **Description**: Implement a static `MDC` wrapper utilizing `ThreadLocal` storage for thread-bound metadata tracking.
- **Acceptance Criteria**:
  - Exposes `put(String key, String value)`, `get(String key)`, `remove(String key)`, and `clear()`.
  - Uses `ThreadLocal<Map<String, String>>` initialized lazily.
  - Thread-safe copy-on-write maps are stored to prevent concurrent access issues.

### Issue 9: Create LogFilter Interface and ThresholdFilter
- **Difficulty**: Easy
- **Labels**: `core`, `feature`
- **Description**: Define a `LogFilter` interface enabling custom logging interception, and implement a `ThresholdFilter`.
- **Acceptance Criteria**:
  - Interface exposes method: `boolean filter(LogEvent event)`.
  - `ThresholdFilter` discards any events whose severity falls below a specified minimum level configuration.

### Issue 10: Implement LoggerContext & LogManager Singleton
- **Difficulty**: Easy
- **Labels**: `core`, `feature`
- **Description**: Build a static `LogManager` management class keeping track of loggers created throughout runtime context.
- **Acceptance Criteria**:
  - Maintains `ConcurrentHashMap` of registered loggers by name.
  - Exposes `getLogger(String name)` and dynamic appender injection during runtime.

### Issue 11: Write AsyncAppender using Virtual Threads
- **Difficulty**: Medium
- **Labels**: `core`, `feature`
- **Description**: Implement an asynchronous logger wrapper using Java 25 Virtual Threads for low-latency non-blocking logging.
- **Acceptance Criteria**:
  - Delegates execution of writing events to a virtual thread-backed ExecutorService.
  - Internal queue buffers up to 1024 logs before dropping or blocking (configurable policy).

### Issue 12: Implement ExceptionFormatter helper
- **Difficulty**: Easy
- **Labels**: `core`, `good-first-issue`, `feature`
- **Description**: Create a utility class `ExceptionFormatter` responsible for formatting exception objects to string layouts.
- **Acceptance Criteria**:
  - Formats standard throwable message and stack trace.
  - Limits trace output line count via configurable option.
  - Sanitizes sensitive class names from class traces.

### Issue 13: Create ConsoleColorFormatter
- **Difficulty**: Easy
- **Labels**: `core`, `feature`
- **Description**: Create a console formatter wrapping log outputs with ANSI escape colors.
- **Acceptance Criteria**:
  - Red color mapping for `ERROR` and `FATAL`.
  - Yellow color mapping for `WARN`.
  - Blue/Green color mapping for `INFO` and `DEBUG`.
  - White color mapping for `TRACE`.

### Issue 14: Create MemoryAppender for Testing
- **Difficulty**: Easy
- **Labels**: `core`, `testing`, `good-first-issue`
- **Description**: Develop a memory appender that collects logged events in an internal array list, intended for test assertions.
- **Acceptance Criteria**:
  - Holds reference list `List<LogEvent>`.
  - Provides method `clear()` and assertion utility checking log presence.

### Issue 15: Create Fluent LoggerContext Configuration Builder
- **Difficulty**: Easy
- **Labels**: `core`, `feature`
- **Description**: Provide a fluent API builder for configuring core log manager programmatically without external files.
- **Acceptance Criteria**:
  - Builder chains like: `LoggerContext.builder().level(INFO).appender(consoleAppender).build()`.

---

## Category 2: Ingestion API & Persistence (`loglite-server` Core)

### Issue 16: Setup Maven Parent and Spring Boot Server Submodule
- **Difficulty**: Easy
- **Labels**: `server`, `chore`, `good-first-issue`
- **Description**: Setup a multi-module Maven build mapping `loglite-core` and parent spring-boot service `loglite-server`.
- **Acceptance Criteria**:
  - Complete `pom.xml` configuration using standard plugins.
  - `loglite-server` spins up a REST backend using Spring Boot.

### Issue 17: Compose Docker Configuration for PostgreSQL
- **Difficulty**: Easy
- **Labels**: `server`, `devops`, `good-first-issue`
- **Description**: Write a local dev dependency Docker Compose definition for running PostgreSQL database.
- **Acceptance Criteria**:
  - `docker-compose.yml` configures PG image 16-alpine.
  - Exposes port 5432 and uses dynamic env parameters for credentials.

### Issue 18: Map LogEntry JPA Entity
- **Difficulty**: Easy
- **Labels**: `server`, `database`, `feature`
- **Description**: Map standard structured logs to a PostgreSQL database table using Hibernate annotation mapping.
- **Acceptance Criteria**:
  - Entity attributes: `id` (UUID), `timestamp` (Instant), `loggerName`, `level`, `message`, `threadName`, `metadata` (JSONB).

### Issue 19: Log Ingestion REST Endpoint
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `feature`
- **Description**: Implement a POST controller endpoint receiving standard JSON single log logs at `/api/v1/logs`.
- **Acceptance Criteria**:
  - Accepts JSON payload representing single `LogEvent`.
  - Returns HTTP status `201 Created` upon successful write.

### Issue 20: Bulk Log Ingestion REST Endpoint
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `feature`
- **Description**: Implement bulk logger insertion controller accepting a payload list at POST `/api/v1/logs/bulk`.
- **Acceptance Criteria**:
  - Receives Array of `LogEvent`.
  - Processes batch transactional writes, returning count and status in response.

### Issue 21: Setup Flyway Database Migration scripts
- **Difficulty**: Easy
- **Labels**: `server`, `database`, `feature`
- **Description**: Initialize database migration files utilizing Flyway/Liquibase to build postgres schemas cleanly.
- **Acceptance Criteria**:
  - Script creates `log_entries` table mapping entity columns.
  - Setup rollback and test schema initialization.

### Issue 22: Configure JSONB MDC mapping in Hibernate
- **Difficulty**: Medium
- **Labels**: `server`, `database`, `feature`
- **Description**: Configure Hibernate attributes mapper to translate MDC string Maps to PostgreSQL JSONB format.
- **Acceptance Criteria**:
  - Converts java `Map<String, String>` directly into JSON string column representation.
  - Metadata keys are queryable on the database using native operators.

### Issue 23: Enable Virtual Threads for Spring MVC Server Ingestion
- **Difficulty**: Easy
- **Labels**: `server`, `performance`
- **Description**: Configure Embedded Tomcat thread pool configurations in Spring Boot backend to leverage virtual threads.
- **Acceptance Criteria**:
  - `spring.threads.virtual.enabled` set to `true`.
  - Log ingestion routes execute inside modern virtual thread contexts.

### Issue 24: Buffer Ingest Logs inside memory queue with Batch Writer
- **Difficulty**: Medium
- **Labels**: `server`, `performance`, `feature`
- **Description**: Implement write buffering mechanism collecting incoming log entries and executing batch statements.
- **Acceptance Criteria**:
  - Flush queue to database when size matches 500 or timeout matches 1 second.
  - Gracefully flush residual queue data upon Spring Application shutdown.

### Issue 25: Add Database migration indexing for Log levels
- **Difficulty**: Easy
- **Labels**: `server`, `database`, `good-first-issue`
- **Description**: Generate schema migration indexing the level column in postgres logs table for optimization queries.
- **Acceptance Criteria**:
  - Index named `idx_log_entries_level` created in Flyway/Liquibase schema.

### Issue 26: Add Database migration indexing for Log Timestamp
- **Difficulty**: Easy
- **Labels**: `server`, `database`, `good-first-issue`
- **Description**: Create schema migration index targetting timestamp descending sorting queries.
- **Acceptance Criteria**:
  - Index named `idx_log_entries_timestamp` created sorting values in DESC direction.

### Issue 27: Add Generalized Inverted Index (GIN) on MDC metadata
- **Difficulty**: Medium
- **Labels**: `server`, `database`, `feature`
- **Description**: Create database GIN index targeting postgres JSONB column to optimize metadata queries.
- **Acceptance Criteria**:
  - Index named `idx_log_entries_metadata_gin` created using JSONB path indexing.

### Issue 28: Fetch log counts grouped by level
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `feature`
- **Description**: Implement backend route GET `/api/v1/logs/stats/levels` returning count counts grouped by log level.
- **Acceptance Criteria**:
  - Query executed via dynamic Spring Data projection or JPA grouping.
  - Return JSON format: `{"INFO": 200, "WARN": 40}`.

### Issue 29: Log Cleanup Cron Job
- **Difficulty**: Easy
- **Labels**: `server`, `feature`
- **Description**: Implement a periodic Spring Scheduler clean job deleting logs older than configured retention policies.
- **Acceptance Criteria**:
  - Scheduled function reads property `loglite.retention.days`.
  - Logs database queries are purged using bulk delete execution.

### Issue 30: Basic Log Ingest API Key validation
- **Difficulty**: Easy
- **Labels**: `server`, `security`, `feature`
- **Description**: Secure ingestion REST endpoints using api key HTTP Header checks.
- **Acceptance Criteria**:
  - Filter intercepting `/api/v1/logs/**` requests.
  - Rejects with `401 Unauthorized` status code if missing headers.

---

## Category 3: Search & Analytics API (`loglite-server` Search)

### Issue 31: Implement Spring Data Log Repository
- **Difficulty**: Easy
- **Labels**: `server`, `database`, `good-first-issue`
- **Description**: Create core repository class handling SQL query capabilities on log table model representation.
- **Acceptance Criteria**:
  - Create interface `LogRepository` extending standard `JpaRepository` and `JpaSpecificationExecutor`.

### Issue 32: Filter logs by time range
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `feature`
- **Description**: Implement time filters parsing start and end instant strings on the log queries endpoint.
- **Acceptance Criteria**:
  - GET `/api/v1/logs?from=...&to=...` processes request filters.
  - Defaults query ranges when date bounds are omitted.

### Issue 33: Filter logs by Logger Category Name
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `good-first-issue`
- **Description**: Allow user searches filtering entries using exact or starts-with queries on Logger Category names.
- **Acceptance Criteria**:
  - Add query param `logger` to REST endpoints.
  - Supports comma-separated multiple categories.

### Issue 34: Dynamic Query Specification Builder
- **Difficulty**: Medium
- **Labels**: `server`, `feature`
- **Description**: Create dynamic JPA dynamic specification builder compiling dynamic criteria models cleanly.
- **Acceptance Criteria**:
  - Generates Hibernate search queries from level, timestamp, metadata parameters.

### Issue 35: Query Metadata JSONB Keys
- **Difficulty**: Medium
- **Labels**: `server`, `database`, `feature`
- **Description**: Allow searching log records by matching attributes buried inside the metadata JSONB column.
- **Acceptance Criteria**:
  - Query syntax like: `/api/v1/logs?metadata.userId=123`.
  - Generates JSONB containment criteria SQL output.

### Issue 36: Query Sorting API
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `good-first-issue`
- **Description**: Provide parameter sorting support on log retrieval searches API.
- **Acceptance Criteria**:
  - Parameters: `sortBy` (timestamp, level) and `sortOrder` (asc, desc).
  - Defaults sorting to timestamp DESC.

### Issue 37: Add pagination controls to Query API
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `feature`
- **Description**: Implement pagination to avoid overflowing REST response payloads.
- **Acceptance Criteria**:
  - Query parameters: `page` (default 0), `size` (default 50).
  - Response wraps payloads in paging context objects containing count, total, page number.

### Issue 38: Filter Logs by string occurrences
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `feature`
- **Description**: Add simple query parameters matching logs search term regex or substring logs.
- **Acceptance Criteria**:
  - Query parameter `search` triggers SQL query `LIKE` or database regex matches.

### Issue 39: Timeline stats grouping logs over interval
- **Difficulty**: Medium
- **Labels**: `server`, `api`, `feature`
- **Description**: Create dashboard analytic aggregation endpoint GET `/api/v1/logs/stats/timeline` showing counts grouped by time frame.
- **Acceptance Criteria**:
  - Group logs inside database hourly or minutely bucket aggregates.
  - Output formats JSON mapping counts grouped in time range buckets.

### Issue 40: Retrieve distinct Service Names
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `good-first-issue`
- **Description**: Implement a simple GET API returning all service identifiers collected in log history tables.
- **Acceptance Criteria**:
  - Route `/api/v1/logs/services` retrieves unique names found in logs table database fields.

### Issue 41: Query correlation logs by transaction id
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `good-first-issue`
- **Description**: Retrieve log lines matching a specific correlation trace identifier metadata property value.
- **Acceptance Criteria**:
  - GET `/api/v1/logs/trace/{traceId}` retrieves all logs mapped with correlation token trace id.

### Issue 42: Filter logs holding exception structures
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `feature`
- **Description**: Create search flags fetching logs mapping throwables or containing string stacks.
- **Acceptance Criteria**:
  - Flag `/api/v1/logs?hasException=true` isolates lines containing runtime exception details.

### Issue 43: Create SQL Console runner endpoint
- **Difficulty**: Medium
- **Labels**: `server`, `security`, `feature`
- **Description**: Provide read-only search capabilities writing SQL directly over aggregated log databases.
- **Acceptance Criteria**:
  - POST `/api/v1/logs/query-sql` runs secure select-only custom statements.
  - Blocks updates, drops, or alters syntax explicitly.

### Issue 44: CSV Export Log Route
- **Difficulty**: Easy
- **Labels**: `server`, `api`, `feature`
- **Description**: Add logs query exporter streaming results as dynamic CSV attachments.
- **Acceptance Criteria**:
  - GET `/api/v1/logs/export/csv` generates response header attachments.
  - Streams rows directly using low-memory footprint builders.

### Issue 45: Setup OpenAPI Documentation using SpringDoc
- **Difficulty**: Easy
- **Labels**: `server`, `chore`, `good-first-issue`
- **Description**: Configure automatic swagger endpoints capturing controller schema mappings.
- **Acceptance Criteria**:
  - Exposes interactive OpenAPI document viewer at `/swagger-ui.html`.

---

## Category 4: CLI Tool (`loglite-cli`) - Local & Remote

### Issue 46: Bootstrap CLI project module utilizing Picocli
- **Difficulty**: Easy
- **Labels**: `cli`, `chore`, `good-first-issue`
- **Description**: Initialize the `loglite-cli` Java Maven project module and bundle standard cli dependencies.
- **Acceptance Criteria**:
  - Maven POM compiles CLI outputs using Picocli plugins.
  - Setup base target run jar configuration.

### Issue 47: Define Main Loglite CLI Commands Class
- **Difficulty**: Easy
- **Labels**: `cli`, `good-first-issue`
- **Description**: Set main execution entrypoint classes providing details on flags.
- **Acceptance Criteria**:
  - Outputs system version details on `--version` CLI call.
  - Formats help text detailing subcommands on `--help`.

### Issue 48: Local API Connection Configuration Command
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Create configuration manager saving target log backend variables to home user folders.
- **Acceptance Criteria**:
  - Command `loglite-cli config --url=... --key=...` saves key-values to a local file.
  - Config directory points to user environment home file `.loglite.json`.

### Issue 49: Retrieve remote logs from CLI
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Write client command connecting server endpoints and retrieving events data stream.
- **Acceptance Criteria**:
  - Subcommand `query` prints received logs to standard output.
  - Incorporates configuration parameters saved under user profile files.

### Issue 50: Relative Date Parsing in CLI Query Bounds
- **Difficulty**: Medium
- **Labels**: `cli`, `feature`
- **Description**: Build relative date parser interpreting flags `--from 2h` or `--to 1d` into valid UTC date strings.
- **Acceptance Criteria**:
  - Converts string duration syntaxes correctly.
  - Returns client validation errors on invalid date-range expressions.

### Issue 51: CLI Level Filtering Argument
- **Difficulty**: Easy
- **Labels**: `cli`, `good-first-issue`
- **Description**: Bind argument parameters filtering log streams by severity options from CLI command line parameters.
- **Acceptance Criteria**:
  - Flag `--level ERROR,WARN` restricts fetched logs to matching arguments.

### Issue 52: Regular Expression match filtering on CLI
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Expose regex parameters query argument options in terminal searches.
- **Acceptance Criteria**:
  - Subcommand flag `--search ".*NullPointer.*"` maps match arguments on requests.

### Issue 53: Metadata JSON search criteria option on CLI
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Allow filtering queried server records using custom metadata identifiers.
- **Acceptance Criteria**:
  - CLI argument syntax `--meta tenantId=alpha` builds valid log criteria filters.

### Issue 54: Limit search pagination CLI configurations
- **Difficulty**: Easy
- **Labels**: `cli`, `good-first-issue`
- **Description**: Implement limit and offset command args supporting search ranges on query calls.
- **Acceptance Criteria**:
  - Subcommand supports flags `--limit` and `--offset`.
  - Rejects invalid pagination indices.

### Issue 55: Validate connection server credentials via CLI Ping command
- **Difficulty**: Easy
- **Labels**: `cli`, `good-first-issue`
- **Description**: Add `ping` CLI command verifying API tokens and reporting health response properties.
- **Acceptance Criteria**:
  - Connects to loglite-server metadata routes.
  - Outputs message if successful.

### Issue 56: Read Local Log files parsing format representations
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Create file parse pipeline unpacking structured file layouts locally.
- **Acceptance Criteria**:
  - Subcommand `parse --file /path/to/log.json` outputs parsed records.

### Issue 57: Filter local text files from CLI
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Enable client query commands searching records directly inside local paths.
- **Acceptance Criteria**:
  - Subcommand execution: `parse --file /path/log.json --level WARN`.
  - Performs in-memory matching of lines.

### Issue 58: Profiles command CLI controller
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Allow configuration commands editing server config profiles mapping local development environments.
- **Acceptance Criteria**:
  - Commands `profile list`, `profile add`, `profile use` store context references.

### Issue 59: Format export to CSV via CLI redirection
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Print queried logs directly formatted inside CSV formats to output redirect pipelines.
- **Acceptance Criteria**:
  - Command: `query --csv` outputs standard structured CSV layout mapping values.

### Issue 60: Upload Local logs batch processing CLI tool
- **Difficulty**: Medium
- **Labels**: `cli`, `feature`
- **Description**: Implement client commands reading raw JSON text logs and routing to ingestion endpoint.
- **Acceptance Criteria**:
  - Execution reads local log file path and imports to `loglite-server` in chunks of 100.

---

## Category 5: CLI Tool (`loglite-cli`) - Tail & Visualization

### Issue 61: Pretty Print output formatting command
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Transform structured backend response formats into neat terminal layouts.
- **Acceptance Criteria**:
  - Output formats line records cleanly: `[LEVEL] HH:mm:ss [Logger] Message`.
  - Exception stack traces are tabbed nested beneath line items.

### Issue 62: Add ANSI color formatting to pretty logs CLI
- **Difficulty**: Easy
- **Labels**: `cli`, `good-first-issue`, `feature`
- **Description**: Use ANSI terminal escape strings colorizing levels differently in console outputs.
- **Acceptance Criteria**:
  - Visual output maps red to `ERROR`, yellow to `WARN`, green to `INFO`.

### Issue 63: Color disable switch command option
- **Difficulty**: Easy
- **Labels**: `cli`, `good-first-issue`
- **Description**: Introduce terminal formatting option toggling ANSI highlights.
- **Acceptance Criteria**:
  - Parameter `--no-color` outputs clean raw plain text files.

### Issue 64: Poll for updates using remote tail command
- **Difficulty**: Medium
- **Labels**: `cli`, `feature`
- **Description**: Build live log streams utilizing continuous background timer queries.
- **Acceptance Criteria**:
  - Command: `query --tail` queries API at defined intervals.
  - Renders only log events published since previous check.

### Issue 65: Connect to server using SSE streams
- **Difficulty**: Medium
- **Labels**: `cli`, `feature`
- **Description**: Implement Server Sent Events inside CLI commands connecting streaming routes.
- **Acceptance Criteria**:
  - Command: `query --live` connects stream channels.
  - Updates render logs immediately as logs are pushed.

### Issue 66: CLI Pager interaction
- **Difficulty**: Medium
- **Labels**: `cli`, `feature`
- **Description**: Configure paging utilities matching CLI query returns to standard terminal paged outputs.
- **Acceptance Criteria**:
  - Interacts with local CLI tools (e.g. `less` on Linux/OSX, `more` on Windows).
  - Falls back to simple chunk logs when system commands are missing.

### Issue 67: Highlighting Query searches on Command Line Output
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Highlight search target matches with yellow colors inside terminal log messages.
- **Acceptance Criteria**:
  - Matches queries inside lines and dynamically embeds styling around key bounds.

### Issue 68: Output results as clean JSON representations
- **Difficulty**: Easy
- **Labels**: `cli`, `good-first-issue`
- **Description**: Support CLI flags mapping response records directly to JSON string outputs.
- **Acceptance Criteria**:
  - Command: `query --format json` prints raw JSON arrays.

### Issue 69: Export parsed markdown summaries from terminal queries
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Support dumping query output layouts as formatted Markdown tables.
- **Acceptance Criteria**:
  - Option `--format md` outputs correct markdown headers and alignments.

### Issue 70: Implement local Log file watcher
- **Difficulty**: Medium
- **Labels**: `cli`, `feature`
- **Description**: Create file observer loop capturing additions on local server paths using Java NIO package functions.
- **Acceptance Criteria**:
  - CLI `watch --file /path/to/log.txt` listens for changes and writes new lines.

### Issue 71: Terminal Text Progress bars of active severities
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Build ASCII stats outputs counting records ratios within selected intervals.
- **Acceptance Criteria**:
  - Commands `stats --levels` outputs text based visual graphs (e.g. `ERROR [===       ] 30%`).

### Issue 72: CLI Selectable Output Columns Argument
- **Difficulty**: Easy
- **Labels**: `cli`, `feature`
- **Description**: Allow user control over which fields to display in CLI tabular formatting.
- **Acceptance Criteria**:
  - Flag `--columns time,level,msg` dynamically hides unspecified fields.

### Issue 73: CLI Startup Setup Wizard
- **Difficulty**: Easy
- **Labels**: `cli`, `good-first-issue`
- **Description**: Launch interactive prompts prompting connection URL details if configs are absent.
- **Acceptance Criteria**:
  - Interactive console queries user inputs, validating before saving credentials.

### Issue 74: Message Truncation switches
- **Difficulty**: Easy
- **Labels**: `cli`, `good-first-issue`
- **Description**: Introduce parameters limiting message length to fit lines in compact terminal grids.
- **Acceptance Criteria**:
  - Flag `--truncate 80` truncates log descriptions, appending ellipsis.

### Issue 75: Auto generate CLI command shells completions
- **Difficulty**: Easy
- **Labels**: `cli`, `chore`
- **Description**: Use Picocli tooling pipelines exporting dynamic autocomplete file definitions.
- **Acceptance Criteria**:
  - Export scripts supporting autocomplete on bash and zsh commands.

---

## Category 6: Framework Integrations & Clients

### Issue 76: Create SLF4J 2.x Binding Provider
- **Difficulty**: Medium
- **Labels**: `integration`, `feature`
- **Description**: Develop `loglite-slf4j` provider mapping SLF4J logs into Loglite events.
- **Acceptance Criteria**:
  - Implement `org.slf4j.spi.SLF4JServiceProvider`.
  - Delegates log calls from standard logging framework calls to `loglite-core` structures.

### Issue 77: Develop Spring Boot Starter configuration module
- **Difficulty**: Easy
- **Labels**: `integration`, `feature`
- **Description**: Create starter project resolving properties and auto-configuring log forwarding beans.
- **Acceptance Criteria**:
  - Auto-configure properties utilizing annotations mapping `loglite` target configs.

### Issue 78: Implement Logback client Appender forwarding logs to HTTP Server
- **Difficulty**: Medium
- **Labels**: `integration`, `feature`
- **Description**: Write Logback Appender translating Logback records and submitting them to HTTP REST server.
- **Acceptance Criteria**:
  - Inherits from standard Logback `AppenderBase<ILoggingEvent>`.
  - Non-blocking client posting JSON batches.

### Issue 79: Standard Java HTTP Servlet log Interceptor Filter
- **Difficulty**: Easy
- **Labels**: `integration`, `feature`
- **Description**: Build web servlet component tracing inbound request metadata and execution performance times.
- **Acceptance Criteria**:
  - Captures methods, URIs, response codes, durations, logging summaries automatically.

### Issue 80: Spring Security authentication correlation log adapter
- **Difficulty**: Easy
- **Labels**: `integration`, `feature`
- **Description**: Bind logged security usernames context attributes automatically into active MDC contexts.
- **Acceptance Criteria**:
  - Extracts principal names from security context holder, updates local MDC keys.

### Issue 81: Ensure Thread Safe implementations of Loggers
- **Difficulty**: Easy
- **Labels**: `core`, `concurrency`
- **Description**: Verify multi-threaded operations do not corrupt state during logger execution.
- **Acceptance Criteria**:
  - Concurrent lock structures check buffer operations in core appenders.

### Issue 82: Implement Spring Boot Aggregator Health Indicators
- **Difficulty**: Easy
- **Labels**: `server`, `feature`
- **Description**: Build custom health indicators verifying storage and backend availability.
- **Acceptance Criteria**:
  - Class implements Spring Boot `HealthIndicator` checking database connections.

### Issue 83: Forward MDC Correlation IDs across Microservices using HTTP headers
- **Difficulty**: Easy
- **Labels**: `integration`, `feature`
- **Description**: Write Http Client interceptors parsing MDC context keys and setting tracing Headers.
- **Acceptance Criteria**:
  - Maps trace headers like `X-Correlation-ID` dynamically during outgoing requests.

### Issue 84: Docker log reader deployment utility
- **Difficulty**: Easy
- **Labels**: `devops`, `good-first-issue`
- **Description**: Write shell script piping docker daemon output formats to server REST endpoints.
- **Acceptance Criteria**:
  - Script loops output logs piping commands to `loglite-cli upload`.

### Issue 85: Kubernetes daemonset collector config template
- **Difficulty**: Easy
- **Labels**: `devops`, `good-first-issue`
- **Description**: Compose sample K8s specifications deploying sidecars shipping logs to servers.
- **Acceptance Criteria**:
  - Manifest outlines setup variables capturing directory maps.

### Issue 86: Java JDK 11 HttpClient logs middleware
- **Difficulty**: Easy
- **Labels**: `integration`, `feature`
- **Description**: Implement custom interceptor tracking execution metrics in standard JDK clients.
- **Acceptance Criteria**:
  - Interceptor records URI targets, response statuses, request execution periods.

### Issue 87: Setup gRPC service logging schema endpoint
- **Difficulty**: Medium
- **Labels**: `server`, `api`, `feature`
- **Description**: Introduce gRPC collection definitions alongside processing server handlers.
- **Acceptance Criteria**:
  - Proto definitions layout schema structure, server consumes high-throughput payloads.

### Issue 88: Ingestion rate-limit filtering configs
- **Difficulty**: Medium
- **Labels**: `server`, `security`, `feature`
- **Description**: Introduce rate limiting guards restricting endpoints from high traffic volumes.
- **Acceptance Criteria**:
  - Rejects callers exceeding bounds using Bucket4j filter logic.

### Issue 89: Webflux Netty non-blocking log collection routing
- **Difficulty**: Medium
- **Labels**: `server`, `performance`, `feature`
- **Description**: Develop high-throughput reactive route alternatives using Webflux handlers.
- **Acceptance Criteria**:
  - REST route ingest options leverage reactive backpressures.

### Issue 90: Thread Pool Context Propagation wrapper
- **Difficulty**: Easy
- **Labels**: `core`, `concurrency`
- **Description**: Build executor wrappers preserving MDC maps on threaded task executions.
- **Acceptance Criteria**:
  - Wrapped runners copy active thread contexts before submitting processes.

---

## Category 7: CI/CD, Build, Docs & Testing

### Issue 91: Build Maven Root configurations linking submodules
- **Difficulty**: Easy
- **Labels**: `build`, `good-first-issue`
- **Description**: Setup root configuration file defining modules and maven compile steps.
- **Acceptance Criteria**:
  - Root `pom.xml` links core, server, and CLI subprojects.

### Issue 92: Configure Postgres Testcontainers in Integration testing
- **Difficulty**: Easy
- **Labels**: `testing`, `good-first-issue`
- **Description**: Setup test environments utilizing dynamic containers for verification databases.
- **Acceptance Criteria**:
  - Integration testing loads Postgres container instances automatically.

### Issue 93: Setup core layout JUnit test cases
- **Difficulty**: Easy
- **Labels**: `testing`, `good-first-issue`
- **Description**: Write test validations checking layouts serialize values consistently.
- **Acceptance Criteria**:
  - Assert core log outputs generate exact JSON structures.

### Issue 94: Test ingestion APIs with WebMvc tests
- **Difficulty**: Easy
- **Labels**: `testing`, `good-first-issue`
- **Description**: Create mock MVC assertions validating routing logic in log servers.
- **Acceptance Criteria**:
  - Validates request payloads formats, response messages code codes.

### Issue 95: Verify CLI commands argument validators
- **Difficulty**: Easy
- **Labels**: `testing`, `good-first-issue`
- **Description**: Write test cases checking Picocli command arguments and output flags.
- **Acceptance Criteria**:
  - Test suites capture stdout / stderr streams testing commands.

### Issue 96: Configure CI Pipeline workflows inside GitHub Actions
- **Difficulty**: Easy
- **Labels**: `devops`, `good-first-issue`
- **Description**: Create automation scripts verifying commits build and tests pass.
- **Acceptance Criteria**:
  - CI pipeline configuration file maps test executions on push.

### Issue 97: Checkstyle XML validation setups
- **Difficulty**: Easy
- **Labels**: `build`, `good-first-issue`
- **Description**: Establish code syntax guidelines using Checkstyle plugins.
- **Acceptance Criteria**:
  - Builds return compilation failures when coding check violations occur.

### Issue 98: Ingestion route performance benchmarks script
- **Difficulty**: Medium
- **Labels**: `devops`
- **Description**: Compose simple load-testing setup evaluating REST ingestion routes.
- **Acceptance Criteria**:
  - Scripts execute load testing reports highlighting throughput limits.

### Issue 99: Release profiles configuration
- **Difficulty**: Easy
- **Labels**: `build`
- **Description**: Map maven release configurations for deploying artifacts to repositories.
- **Acceptance Criteria**:
  - Profile signs files, builds javadoc/sources attachments.

### Issue 100: Build API Javadoc documents
- **Difficulty**: Easy
- **Labels**: `documentation`, `good-first-issue`
- **Description**: Run and generate comprehensive library Javadoc outputs across core files.
- **Acceptance Criteria**:
  - Resolve compiler documentation warning reports.

### Issue 101: Write core README project details
- **Difficulty**: Easy
- **Labels**: `documentation`, `good-first-issue`
- **Description**: Compose project main instructions detailing build sequences and tools.
- **Acceptance Criteria**:
  - Explains components, running locally via Docker, using CLI commands.

### Issue 102: Write project Quickstart documentation guide
- **Difficulty**: Easy
- **Labels**: `documentation`, `good-first-issue`
- **Description**: Outline introductory instructions to configure and stream logging.
- **Acceptance Criteria**:
  - Step-by-step tutorial importing logger binding libraries.

### Issue 103: Catalog CLI command options references
- **Difficulty**: Easy
- **Labels**: `documentation`, `good-first-issue`
- **Description**: Write detailed documentation enumerating command arguments and output formats.
- **Acceptance Criteria**:
  - Formats commands list showing flags options.

### Issue 104: Setup Jacoco Coverage constraints check
- **Difficulty**: Easy
- **Labels**: `build`, `good-first-issue`
- **Description**: Bind execution plugins checking test coverage metrics during build steps.
- **Acceptance Criteria**:
  - Compilations return errors if test coverages fall below defined metrics.

### Issue 105: Maven Enforcer Dependency Convergence validation
- **Difficulty**: Easy
- **Labels**: `build`
- **Description**: Verify version configurations do not conflict inside active dependencies lists.
- **Acceptance Criteria**:
  - Maven builds reject duplicate imports with different versions.
