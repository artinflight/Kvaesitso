#!/usr/bin/env bash
set -euo pipefail

# Copy this file to scripts/preview.sh in a target repo, make it executable,
# then replace the placeholders below with the repo's real preview command,
# URLs, and expected response marker.

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

PREVIEW_NAME="${PREVIEW_NAME:-REPLACE_WITH_REPO_PREVIEW_NAME}"
PORT="${PORT:-REPLACE_WITH_PORT}"
HOST="${HOST:-0.0.0.0}"
LOCAL_URL="${LOCAL_URL:-http://127.0.0.1:${PORT}/}"
OPERATOR_URL="${OPERATOR_URL:-REPLACE_WITH_OPERATOR_URL}"
EXPECTED_MARKER="${EXPECTED_MARKER:-REPLACE_WITH_TEXT_OR_PATTERN_EXPECTED_IN_RESPONSE}"
SESSION="${SESSION:-${PREVIEW_NAME}-preview}"
START_COMMAND="${START_COMMAND:-REPLACE_WITH_START_COMMAND}"

usage() {
  printf 'Usage: %s [start|verify|status|stop]\n' "$0"
}

check_config() {
  case "$PORT $OPERATOR_URL $EXPECTED_MARKER $START_COMMAND" in
    *REPLACE_WITH*)
      printf 'scripts/preview.sh has not been customized for this repo.\n' >&2
      printf 'Fill in PORT, OPERATOR_URL, EXPECTED_MARKER, and START_COMMAND.\n' >&2
      exit 2
      ;;
  esac
}

check_url() {
  local url="$1"
  curl -fsS "$url" | rg "$EXPECTED_MARKER" >/dev/null
}

start_preview() {
  check_config
  if ! tmux has-session -t "$SESSION" 2>/dev/null; then
    tmux new-session -d -s "$SESSION" -c "$ROOT" "$START_COMMAND"
  fi
  verify_preview
  printf 'Preview running via %s\n' "$SESSION"
  printf 'Local: %s\n' "$LOCAL_URL"
  printf 'Operator: %s\n' "$OPERATOR_URL"
  printf 'Preview URL:: Updated [Open preview](%s)\n' "$OPERATOR_URL"
}

verify_preview() {
  check_config
  check_url "$LOCAL_URL"
  check_url "$OPERATOR_URL"
}

status_preview() {
  ss -ltnp | rg ":${PORT}\\b" || true
  tmux has-session -t "$SESSION" 2>/dev/null && printf 'tmux:%s active\n' "$SESSION" || true
}

stop_preview() {
  tmux kill-session -t "$SESSION" 2>/dev/null || true
  printf 'Preview stopped.\n'
}

case "${1:-start}" in
  start) start_preview ;;
  verify) verify_preview ;;
  status) status_preview ;;
  stop) stop_preview ;;
  -h|--help|help) usage ;;
  *) usage >&2; exit 2 ;;
esac
