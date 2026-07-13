#!/usr/bin/env bash
# Simple load-testing script for loglite-server's ingestion routes.
# Fires concurrent bulk-ingest requests and reports throughput.
#
# Usage:
#   ./ingest-load-test.sh <server-url> <api-key> [requests] [concurrency] [batch-size]
#
# Requires: curl
set -euo pipefail

SERVER_URL="${1:?Usage: $0 <server-url> <api-key> [requests] [concurrency] [batch-size]}"
API_KEY="${2:?Usage: $0 <server-url> <api-key> [requests] [concurrency] [batch-size]}"
REQUESTS="${3:-100}"
CONCURRENCY="${4:-10}"
BATCH_SIZE="${5:-50}"

build_batch() {
  local ts
  ts="$(date -u +%Y-%m-%dT%H:%M:%S.%3NZ)"
  local entries=()
  for ((i = 0; i < BATCH_SIZE; i++)); do
    entries+=("{\"timestamp\":\"$ts\",\"loggerName\":\"load-test\",\"level\":\"INFO\",\"message\":\"load test entry $i\"}")
  done
  local IFS=,
  echo "[${entries[*]}]"
}

send_one() {
  curl -s -o /dev/null -w "%{http_code} %{time_total}\n" \
    -X POST "$SERVER_URL/api/v1/logs/bulk" \
    -H "Content-Type: application/json" \
    -H "X-API-Key: $API_KEY" \
    -d "$(build_batch)"
}
export -f send_one build_batch
export SERVER_URL API_KEY BATCH_SIZE

echo "Sending $REQUESTS requests (batch size $BATCH_SIZE) at concurrency $CONCURRENCY to $SERVER_URL ..."
start=$(date +%s)

seq "$REQUESTS" | xargs -P "$CONCURRENCY" -I {} bash -c 'send_one' > /tmp/loglite-load-test-results.txt

end=$(date +%s)
elapsed=$((end - start))

total_entries=$((REQUESTS * BATCH_SIZE))
success=$(grep -c '^2' /tmp/loglite-load-test-results.txt || true)

echo "Done in ${elapsed}s: $REQUESTS requests, $success succeeded, ~$total_entries log entries ingested."
if [ "$elapsed" -gt 0 ]; then
  echo "Throughput: ~$((total_entries / elapsed)) entries/sec"
fi
