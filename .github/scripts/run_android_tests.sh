#!/usr/bin/env bash
set -euo pipefail

PKGS="${TEST_PACKAGES:-}"
if [ -z "$PKGS" ]; then
    ./gradlew connectedDebugAndroidTest --stacktrace
else
    ARGS=$(echo "$PKGS" | tr ' ' '\n' | grep -v '^$' | sed 's/$/.*/;s/^/--tests /' | tr '\n' ' ')
    ./gradlew connectedDebugAndroidTest $ARGS --stacktrace
fi
