#!/usr/bin/env bash
# Tails a Docker container's log output and forwards each line to loglite-server
# via `loglite-cli upload`.
#
# Usage:
#   ./docker-log-reader.sh <container-name-or-id> [logger-name]
#
# Requires: docker, loglite-cli (configured with `loglite-cli config --url ... --key ...`)
set -euo pipefail

CONTAINER="${1:?Usage: $0 <container-name-or-id> [logger-name]}"
LOGGER_NAME="${2:-$CONTAINER}"

TMP_FILE="$(mktemp)"
trap 'rm -f "$TMP_FILE"' EXIT

docker logs -f --since 0s "$CONTAINER" | while IFS= read -r line; do
  timestamp="$(date -u +%Y-%m-%dT%H:%M:%S.%3NZ)"
  escaped_line="${line//\"/\\\"}"
  printf '{"timestamp":"%s","loggerName":"%s","level":"INFO","message":"%s"}\n' \
    "$timestamp" "$LOGGER_NAME" "$escaped_line" >> "$TMP_FILE"

  if [ "$(wc -l < "$TMP_FILE")" -ge 100 ]; then
    loglite-cli upload --file "$TMP_FILE"
    : > "$TMP_FILE"
  fi
done
