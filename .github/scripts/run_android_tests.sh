#!/usr/bin/env bash
set -euo pipefail

PKGS="${TEST_PACKAGES:-}"
if [ -z "$PKGS" ]; then
    ./gradlew connectedDebugAndroidTest --stacktrace
else
    # connectedDebugAndroidTest uses runner args for filtering, not --tests
    PKG_FILTER=$(echo "$PKGS" | tr ' ' '\n' | grep -v '^$' | tr '\n' ',' | sed 's/,$//')
    ./gradlew connectedDebugAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.package="$PKG_FILTER" \
        --stacktrace
fi
