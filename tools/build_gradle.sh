#!/usr/bin/env bash
# build_gradle.sh — build all Gradle-based LLD solutions
#
# Requires: Java 21+, gradlew wrapper in each solution dir
# Run from repo root:  bash tools/build_gradle.sh
# Or via master:       bash tools/build_all.sh
#
# Fixes applied to make these compile on Java 21 + Gradle 8.5:
#   - gradle-wrapper.properties upgraded from 5.2.1 → 8.5
#   - compile/testCompile → implementation/testImplementation
#   - Lombok 1.18.x → 1.18.42
#   - Dead JCenter deps migrated to JitPack (see design-true-caller)
#   - Spring Boot 2.2.6 → 2.7.18 (distributed-id-gen)
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SOLS="$REPO_ROOT/solutions"

GRADLE_PROJECTS=(
  "design-true-caller/solution-1"
  "design-snake-and-ladder-game/solution-1"
  "design-bill-sharingexpense-sharing-like-splitwise/solution-1"
  "design-amazon-locker-service/solution-1"
  "design-vehicle-car-rental-application-like-zoomcar/solution-1"
  "design-parking-lot-system/solution-2"
  "cricinfocricbuzz/solution-2"
  "e-commerce-review-system/solution-1"
  "design-distributed-id-generation-like-twitter-snowflake-id/solution-1"
)

PASS=0; FAIL=0

echo ""
echo "🔨 Building Gradle projects (Java 21, Gradle 8.5, tests skipped)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

for proj in "${GRADLE_PROJECTS[@]}"; do
  dir="$SOLS/$proj"
  if [ ! -d "$dir" ]; then
    echo "  ⚠️  MISSING  $proj  (run tools/clone_solutions.py first)"
    continue
  fi
  printf "  %-65s" "$proj"
  output=$(cd "$dir" && ./gradlew build -x test --quiet 2>&1)
  if [ $? -eq 0 ]; then
    echo "✅"
    (( PASS++ )) || true
  else
    echo "❌"
    echo "$output" | grep -E "error:|Error:|What went wrong" | head -3 | sed 's/^/    /'
    (( FAIL++ )) || true
  fi
done

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Gradle: $PASS passed, $FAIL failed"
echo ""
[ "$FAIL" -eq 0 ]   # exit non-zero if any failed
