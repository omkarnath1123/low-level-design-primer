#!/usr/bin/env python3
"""
Parse solutions.md, create a directory per problem, and clone
all GitHub solution repos into solution-N subdirectories.

Non-GitHub links (articles, blogs, leetcode, etc.) are noted but skipped.
Dirs are still created even when no GitHub solutions exist.
"""

import re
import os
import subprocess
from pathlib import Path

REPO_ROOT = Path(__file__).parent
SOLUTIONS_MD = REPO_ROOT / "solutions.md"
SOLUTIONS_DIR = REPO_ROOT / "solutions"

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def slugify(name: str) -> str:
    """Problem name → lowercase, hyphen-separated, filesystem-safe slug."""
    name = re.sub(r"[^\w\s-]", "", name.lower())
    name = re.sub(r"[\s/]+", "-", name.strip())
    name = re.sub(r"-+", "-", name)
    return name.strip("-")


def repo_root_url(github_url: str) -> str:
    """
    Strip any /tree/…, /blob/… path suffix so we always clone the repo root.
    e.g. https://github.com/user/repo/tree/master/subdir → https://github.com/user/repo
    """
    match = re.match(r"(https://github\.com/[^/\s]+/[^/\s#?]+)", github_url)
    if match:
        return match.group(1).rstrip("/")
    return github_url


# ---------------------------------------------------------------------------
# Parser
# ---------------------------------------------------------------------------

def parse_problems(md_path: Path) -> list[dict]:
    """
    Parse the markdown table in solutions.md.

    Returns a list of dicts:
        {
            name: str,                  # raw problem name
            slug: str,                  # filesystem-safe name
            github_links: list[tuple],  # [(label, repo_url), ...]
        }
    """
    content = md_path.read_text(encoding="utf-8")
    problems: list[dict] = []

    # Each table row: | col1 | col2 | col3 |
    # Some rows have a leading space before the first pipe — allow for that.
    row_re = re.compile(r"^\s*\|(.+?)\|(.+?)(?:\|.+?)?\|?\s*$", re.MULTILINE)

    for m in row_re.finditer(content):
        name_cell = m.group(1).strip()
        solutions_cell = m.group(2).strip()

        # Skip header / separator rows
        if not name_cell or ":-" in name_cell or name_cell.lower().startswith("question"):
            continue

        # Strip markdown links from name cell (shouldn't be any, but just in case)
        name = re.sub(r"\[([^\]]+)\]\([^\)]+\)", r"\1", name_cell).strip()
        name = re.sub(r"\s+", " ", name)
        if not name:
            continue

        slug = slugify(name)

        # Collect GitHub repo URLs from solutions cell only
        github_links: list[tuple[str, str]] = []
        seen_repos: set[str] = set()

        for label, url in re.findall(
            r"\[([^\]]+)\]\((https?://[^\)\s]+)\)", solutions_cell
        ):
            if "github.com" not in url:
                continue
            repo_url = repo_root_url(url)
            if repo_url in seen_repos:
                continue
            seen_repos.add(repo_url)
            github_links.append((label, repo_url))

        problems.append(
            {"name": name, "slug": slug, "github_links": github_links}
        )

    return problems


# ---------------------------------------------------------------------------
# Git clone
# ---------------------------------------------------------------------------

def clone(repo_url: str, dest: Path) -> bool:
    """Shallow-clone repo_url into dest. Returns True on success."""
    env = os.environ.copy()

    result = subprocess.run(
        ["git", "clone", "--depth=1", repo_url, str(dest)],
        capture_output=True,
        text=True,
        env=env,
        timeout=180,
    )

    if result.returncode == 0:
        print(f"        ✅ cloned  → {dest.name}")
        return True

    print(f"        ❌ failed  → {repo_url}")
    err = result.stderr.strip().splitlines()
    for line in err[-3:]:          # show last 3 lines of git error
        print(f"           {line}")
    return False


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main() -> None:
    problems = parse_problems(SOLUTIONS_MD)
    SOLUTIONS_DIR.mkdir(exist_ok=True)

    total_dirs = 0
    total_cloned = 0
    total_failed = 0

    print(f"\n🐾 low-level-design-primer solution cloner")
    print(f"   Solutions dir: {SOLUTIONS_DIR}\n")
    print(f"{'─' * 60}")

    for prob in problems:
        prob_dir = SOLUTIONS_DIR / prob["slug"]
        prob_dir.mkdir(exist_ok=True)
        total_dirs += 1

        links = prob["github_links"]
        status = f"({len(links)} repo{'s' if len(links) != 1 else ''})" if links else "(no GitHub solutions)"
        print(f"\n📂  {prob['name']}")
        print(f"    dir: solutions/{prob['slug']}  {status}")

        for i, (label, repo_url) in enumerate(links, start=1):
            sol_dir = prob_dir / f"solution-{i}"

            if sol_dir.exists():
                print(f"        ⏭️  solution-{i} already exists, skipping")
                continue

            print(f"        🔄 [{label}] {repo_url}")
            ok = clone(repo_url, sol_dir)
            if ok:
                total_cloned += 1
            else:
                total_failed += 1

    print(f"\n{'─' * 60}")
    print(f"✅ Done!")
    print(f"   Dirs created : {total_dirs}")
    print(f"   Repos cloned : {total_cloned}")
    if total_failed:
        print(f"   ❌ Failed    : {total_failed}  (check output above)")
    print()


if __name__ == "__main__":
    main()
