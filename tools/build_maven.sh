#!/usr/bin/env bash
# build_maven.sh — build all Maven-based LLD solutions
#
# Requires: Java 21+, Maven 3.x on PATH
# Run from repo root:  bash tools/build_maven.sh
# Or via master:       bash tools/build_all.sh
#
# Fixes applied to make these compile on Java 21:
#   - Lombok 1.18.x → 1.18.42 in every pom.xml
#   - annotationProcessorPaths added to maven-compiler-plugin (Java 17+ required)
#   - source/target 7 → 8  (Java 7 bytecode rejected by modern javac)
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SOLS="$REPO_ROOT/solutions"

# Plain Maven projects (no wrapper — use system mvn)
MVN_PROJECTS=(
  "design-parking-lot-system/solution-1"
  "design-cache-system/solution-1"
  "design-chess-game/solution-1"
  "movie-ticket-booking/solution-1"
  "uber-eats-door-dash-swiggy/solution-1"
)

# Projects that ship their own ./mvnw wrapper
MVNW_PROJECTS=(
  "cab-booking-like-uber-ola/solution-1"
)

# Nested sub-project (Maven inside a mixed-content directory)
NESTED_PROJECTS=(
  "implement-a-logger/solution-1/java-projects"
)

PASS=0; FAIL=0

_build_maven() {
  local dir="$1" cmd="$2"
  printf "  %-65s" "${dir#$SOLS/}"
  output=$(cd "$dir" && $cmd package -DskipTests -q 2>&1)
  if [ $? -eq 0 ]; then
    echo "✅"
    (( PASS++ )) || true
  else
    echo "❌"
    echo "$output" | grep "\[ERROR\]" | head -4 | sed 's/^/    /'
    (( FAIL++ )) || true
  fi
}

echo ""
echo "🔨 Building Maven projects (Java 21, tests skipped)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

for proj in "${MVN_PROJECTS[@]}"; do
  dir="$SOLS/$proj"
  [ -d "$dir" ] || { echo "  ⚠️  MISSING  $proj"; continue; }
  _build_maven "$dir" "mvn"
done

for proj in "${MVNW_PROJECTS[@]}"; do
  dir="$SOLS/$proj"
  [ -d "$dir" ] || { echo "  ⚠️  MISSING  $proj"; continue; }
  _build_maven "$dir" "./mvnw"
done

for proj in "${NESTED_PROJECTS[@]}"; do
  dir="$SOLS/$proj"
  [ -d "$dir" ] || { echo "  ⚠️  MISSING  $proj"; continue; }
  _build_maven "$dir" "mvn"
done

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Maven: $PASS passed, $FAIL failed"
echo ""
[ "$FAIL" -eq 0 ]
