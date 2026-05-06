#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

if [[ -x "./gradlew" ]]; then
  exec ./gradlew --no-daemon run "$@"
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle --no-daemon run "$@"
fi

cat >&2 <<'EOF'
Could not find Gradle to run the app.

Options:
  1) Add the Gradle wrapper to this repo:
       gradle wrapper
     Then re-run:
       ./run.sh

  2) Install Gradle system-wide and re-run:
       ./run.sh
EOF
exit 1
