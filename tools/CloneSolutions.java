import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * CloneSolutions.java — Java 21 port of clone_solutions.py
 *
 * Parses solutions.md, creates a directory per problem, and shallow-clones
 * all GitHub solution repos into solution-N subdirectories.
 *
 * Non-GitHub links (articles, blogs, leetcode, medium…) are skipped.
 * Problem dirs are still created even when no GitHub solutions exist.
 *
 * Run with:  java tools/CloneSolutions.java      (from repo root)
 * Requires:  Java 21+,  git on PATH
 * Note:      CWD must be the repo root — solutions.md and solutions/ are resolved from there
 */
public class CloneSolutions {

    // -------------------------------------------------------------------------
    // Data model
    // -------------------------------------------------------------------------

    record GithubLink(String label, String url) {}

    record Problem(String name, String slug, List<GithubLink> githubLinks) {}

    // -------------------------------------------------------------------------
    // Compiled regex patterns (DRY: defined once, reused everywhere)
    // -------------------------------------------------------------------------

    /** Markdown table row — tolerates an optional leading space before the first | */
    private static final Pattern ROW = Pattern.compile(
            "^\\s*\\|(.+?)\\|(.+?)(?:\\|.+?)?\\|?\\s*$",
            Pattern.MULTILINE
    );

    /** Inline markdown link: [label](url) */
    private static final Pattern LINK = Pattern.compile(
            "\\[([^\\]]+)]\\((https?://[^)\\s]+)\\)"
    );

    /** Captures https://github.com/owner/repo, stopping before /tree /blob /# /? */
    private static final Pattern GITHUB_REPO = Pattern.compile(
            "(https://github\\.com/[^/\\s]+/[^/\\s#?]+)"
    );

    /** Characters that are NOT word-chars, spaces, or hyphens — removed when slugifying */
    private static final Pattern NON_SLUG_CHAR = Pattern.compile("[^\\w\\s-]");

    /** Whitespace or slashes → single hyphen when slugifying */
    private static final Pattern WHITESPACE_OR_SLASH = Pattern.compile("[\\s/]+");

    /** Multiple consecutive hyphens → one hyphen */
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Problem name → lowercase, hyphen-separated, filesystem-safe slug.
     * e.g. "Design Bill Sharing/Expense Sharing like Splitwise"
     *   →  "design-bill-sharingexpense-sharing-like-splitwise"
     */
    static String slugify(String name) {
        var s = NON_SLUG_CHAR.matcher(name.toLowerCase()).replaceAll("");
        s = WHITESPACE_OR_SLASH.matcher(s.strip()).replaceAll("-");
        s = MULTI_HYPHEN.matcher(s).replaceAll("-");
        return s.replaceAll("^-+|-+$", "");    // trim leading/trailing hyphens
    }

    /**
     * Strip /tree/…, /blob/… path suffixes so we always clone the repo root.
     * e.g. https://github.com/user/repo/tree/master/subdir
     *   →  https://github.com/user/repo
     */
    static String repoRootUrl(String githubUrl) {
        var m = GITHUB_REPO.matcher(githubUrl);
        return m.find() ? m.group(1).replaceAll("/+$", "") : githubUrl;
    }

    // -------------------------------------------------------------------------
    // Parser
    // -------------------------------------------------------------------------

    /**
     * Parse the markdown table in solutions.md.
     * Returns one {@link Problem} per valid table row, skipping headers/separators.
     */
    static List<Problem> parseProblems(Path mdPath) throws IOException {
        var content  = Files.readString(mdPath);
        var problems = new ArrayList<Problem>();
        var rowMatcher = ROW.matcher(content);

        while (rowMatcher.find()) {
            var nameCell      = rowMatcher.group(1).strip();
            var solutionsCell = rowMatcher.group(2).strip();

            // Skip header row and separator rows (e.g. | :---: | :-: |)
            if (nameCell.isBlank()
                    || nameCell.contains(":-")
                    || nameCell.toLowerCase().startsWith("question")) {
                continue;
            }

            // Strip any markdown links from the name cell, normalise whitespace
            var name = LINK.matcher(nameCell).replaceAll("$1").strip();
            name = name.replaceAll("\\s+", " ");
            if (name.isBlank()) continue;

            // Collect GitHub links from the solutions cell, deduplicated by repo root URL
            var githubLinks = new ArrayList<GithubLink>();
            var seenRepos   = new LinkedHashSet<String>();    // preserves insertion order
            var linkMatcher = LINK.matcher(solutionsCell);

            while (linkMatcher.find()) {
                var label = linkMatcher.group(1);
                var url   = linkMatcher.group(2);

                if (!url.contains("github.com")) continue;

                var repoUrl = repoRootUrl(url);
                if (seenRepos.add(repoUrl)) {   // add() returns false when already present
                    githubLinks.add(new GithubLink(label, repoUrl));
                }
            }

            problems.add(new Problem(name, slugify(name), List.copyOf(githubLinks)));
        }

        return List.copyOf(problems);
    }

    // -------------------------------------------------------------------------
    // Git clone
    // -------------------------------------------------------------------------

    /**
     * Shallow-clone {@code repoUrl} into {@code dest}.
     *
     * Uses {@code --depth=1} to keep things fast and lean.
     * stdout and stderr from git are merged and drained to prevent pipe-full
     * deadlocks; only the last few error lines are printed on failure.
     *
     * @return true on success, false on any failure
     */
    static boolean clone(String repoUrl, Path dest) {
        try {
            var process = new ProcessBuilder(
                    "git", "clone", "--depth=1", repoUrl, dest.toString()
            )
                    .redirectErrorStream(true)   // merge stderr → stdout; prevents deadlock
                    .start();

            // Drain output BEFORE waitFor() to avoid pipe-buffer deadlock
            var output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("        \u2705 cloned  \u2192 " + dest.getFileName());
                return true;
            }

            System.out.println("        \u274c failed  \u2192 " + repoUrl);
            // Show last 3 non-blank lines of git's output for context
            output.lines()
                    .filter(l -> !l.isBlank())
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new))
                    .stream()
                    .skip(Math.max(0, output.lines().filter(l -> !l.isBlank()).count() - 3))
                    .forEach(l -> System.out.println("           " + l));
            return false;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("        \u274c interrupted \u2192 " + repoUrl);
            return false;
        } catch (IOException e) {
            System.out.println("        \u274c error \u2192 " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) throws IOException {
        // Resolve paths relative to CWD — run this file from the repo root
        var repoRoot     = Path.of(System.getProperty("user.dir"));
        var solutionsMd  = repoRoot.resolve("solutions.md");
        var solutionsDir = repoRoot.resolve("solutions");

        var problems = parseProblems(solutionsMd);
        Files.createDirectories(solutionsDir);

        int totalDirs = 0, totalCloned = 0, totalFailed = 0;
        var sep = "\u2500".repeat(60);   // ─────

        System.out.println("\n\uD83D\uDC3E low-level-design-primer solution cloner");
        System.out.println("   Solutions dir: " + solutionsDir);
        System.out.println();
        System.out.println(sep);

        for (var prob : problems) {
            var probDir = solutionsDir.resolve(prob.slug());
            Files.createDirectories(probDir);
            totalDirs++;

            var links  = prob.githubLinks();
            var status = links.isEmpty()
                    ? "(no GitHub solutions)"
                    : "(%d repo%s)".formatted(links.size(), links.size() == 1 ? "" : "s");

            System.out.println("\n\uD83D\uDCC2  " + prob.name());
            System.out.println("    dir: solutions/" + prob.slug() + "  " + status);

            for (int i = 0; i < links.size(); i++) {
                var link   = links.get(i);
                var solDir = probDir.resolve("solution-" + (i + 1));

                if (Files.exists(solDir)) {
                    System.out.println("        \u23ED\uFE0F  solution-" + (i + 1) + " already exists, skipping");
                    continue;
                }

                System.out.println("        \uD83D\uDD04 [" + link.label() + "] " + link.url());
                if (clone(link.url(), solDir)) {
                    totalCloned++;
                } else {
                    totalFailed++;
                }
            }
        }

        System.out.println("\n" + sep);
        System.out.println("\u2705 Done!");
        System.out.println("   Dirs created : " + totalDirs);
        System.out.println("   Repos cloned : " + totalCloned);
        if (totalFailed > 0) {
            System.out.println("   \u274c Failed    : " + totalFailed + "  (check output above)");
        }
        System.out.println();
    }
}
