# loglite-cli command reference

Global options (available on every command): `-h, --help`, `-V, --version`.

## config

Save connection settings for the active profile.

| Flag | Description |
| --- | --- |
| `--url` | Loglite server base URL, e.g. `http://localhost:8080` |
| `--key` | API key used to authenticate requests |

## profile

Manage named connection profiles.

- `profile list` — list all configured profiles.
- `profile add <name> [--url] [--key]` — add or update a named profile.
- `profile use <name>` — switch the active profile.

## ping

Check connectivity and credentials against the configured server. No options.

## query

Query logs from the configured server.

| Flag | Description |
| --- | --- |
| `--from` | Start of the time range: ISO-8601 instant or relative duration (e.g. `2h`, `1d`) |
| `--to` | End of the time range: ISO-8601 instant or relative duration (e.g. `30m`) |
| `--level` | Comma-separated levels to include, e.g. `ERROR,WARN` |
| `--search` | Regular expression matched against each log message |
| `--meta` | Metadata filter as `key=value`, repeatable, e.g. `--meta tenantId=alpha` |
| `--limit` | Maximum number of results to return (default `50`) |
| `--offset` | Number of results to skip (default `0`) |
| `--no-color` | Disable ANSI color output |
| `--columns` | Comma-separated fields to display: `time,level,logger,msg,thread` |
| `--truncate` | Maximum message length before truncating with an ellipsis |
| `--format` | Output format: `pretty` (default), `json`, `csv`, `md` |
| `--pager` | Pipe pretty output through the system pager (`less`/`more`) |
| `--tail` | Poll the server at intervals and print only newly arrived logs |
| `--tail-interval` | Seconds between polls when using `--tail` (default `2`) |
| `--live` | Connect to the server's log stream (SSE) and print events as they arrive |

## parse

Parse a local newline-delimited JSON log file.

| Flag | Description |
| --- | --- |
| `--file` | Path to a newline-delimited JSON log file (required) |
| `--level` | Comma-separated levels to include |

## watch

Watch a local log file for new lines as they are appended.

| Flag | Description |
| --- | --- |
| `--file` | Path to the log file to watch (required) |

## upload

Upload a local JSON log file to the server in batches of 100.

| Flag | Description |
| --- | --- |
| `--file` | Path to a newline-delimited JSON log file (required) |

## stats

Show aggregate statistics about ingested logs.

| Flag | Description |
| --- | --- |
| `--levels` | Show counts by severity level as ASCII bars |

## setup

Interactively prompt for and save the server URL and API key. No options.

## completion

Generate a bash/zsh completion script to stdout:

```bash
loglite-cli completion > loglite-cli_completion.sh
source loglite-cli_completion.sh
```
