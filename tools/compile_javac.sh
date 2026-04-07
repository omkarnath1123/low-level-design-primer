#!/usr/bin/env bash
# compile_javac.sh — compile raw-javac solutions (no build system)
#
# Requires: Java 21+ (compiles to --release 8 bytecode for compatibility)
# Run from repo root:  bash tools/compile_javac.sh
# Or via master:       bash tools/build_all.sh
#
# Currently handles:
#   cricinfocricbuzz/solution-1  — two sub-modules: CricketPortal, MovieTicketBooking
#     (skeleton code; stub classes + import fixes applied to make it compile)
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SOLS="$REPO_ROOT/solutions"

PASS=0; FAIL=0

_compile_module() {
  local label="$1" src_dir="$2" out_dir="$3"
  mkdir -p "$out_dir"
  printf "  %-65s" "$label"
  # shellcheck disable=SC2046
  output=$(javac --release 8 -d "$out_dir" $(find "$src_dir" -name "*.java") 2>&1)
  if [ $? -eq 0 ]; then
    echo "✅"
    (( PASS++ )) || true
  else
    echo "❌"
    echo "$output" | grep "error:" | head -5 | sed 's/^/    /'
    (( FAIL++ )) || true
  fi
}

echo ""
echo "🔨 Compiling raw-javac solutions (--release 8)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

BASE="$SOLS/cricinfocricbuzz/solution-1"
if [ -d "$BASE" ]; then
  _compile_module \
    "cricinfocricbuzz/solution-1/CricketPortal" \
    "$BASE/CricketPortal/src" \
    "$BASE/CricketPortal/out"

  _compile_module \
    "cricinfocricbuzz/solution-1/MovieTicketBooking" \
    "$BASE/MovieTicketBooking/src" \
    "$BASE/MovieTicketBooking/out"
else
  echo "  ⚠️  MISSING  cricinfocricbuzz/solution-1  (run tools/clone_solutions.py first)"
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Javac: $PASS passed, $FAIL failed"
echo ""
[ "$FAIL" -eq 0 ]
