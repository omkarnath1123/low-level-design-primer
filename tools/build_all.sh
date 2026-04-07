#!/usr/bin/env bash
# build_all.sh — build EVERY LLD solution in this repo
#
# Orchestrates:
#   1. Gradle projects   (tools/build_gradle.sh)
#   2. Maven projects    (tools/build_maven.sh)
#   3. Raw javac modules (tools/compile_javac.sh)
#
# Prerequisites:
#   - Java 21+  (java -version)
#   - Maven 3.x (mvn -version)
#   - git on PATH
#   - Solutions already cloned:  python3 tools/clone_solutions.py
#
# Usage:
#   bash tools/build_all.sh            # all three steps
#   bash tools/build_gradle.sh         # Gradle only
#   bash tools/build_maven.sh          # Maven only
#   bash tools/compile_javac.sh        # raw javac only
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo ""
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║       🐾  Kulfi — LLD Primer full build                     ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""
echo "  Java  : $(java -version 2>&1 | head -1)"
echo "  Maven : $(mvn -version 2>&1 | head -1)"
echo ""

TOTAL_FAIL=0

run_step() {
  local script="$1"
  bash "$script" || (( TOTAL_FAIL++ )) || true
}

run_step "$SCRIPT_DIR/build_gradle.sh"
run_step "$SCRIPT_DIR/build_maven.sh"
run_step "$SCRIPT_DIR/compile_javac.sh"

echo "╔══════════════════════════════════════════════════════════════╗"
if [ "$TOTAL_FAIL" -eq 0 ]; then
  echo "║  ✅  All builds passed!                                      ║"
else
  echo "║  ❌  $TOTAL_FAIL build step(s) failed — see output above       ║"
fi
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""
[ "$TOTAL_FAIL" -eq 0 ]
