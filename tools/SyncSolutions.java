import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * SyncSolutions.java — Java 21 script
 *
 * Runs  git fetch -p  then  git pull  for every solution repo
 * found under solutions/ (any dir containing a .git directory).
 *
 * Run from repo root:  java tools/SyncSolutions.java
 * Requires:            Java 21+,  git on PATH,  network access
 */
public class SyncSolutions {

    // ── Data ──────────────────────────────────────────────────────────────────

    enum Status { OK, FETCH_FAILED, PULL_FAILED }

    record RepoResult(
            String   label,          // e.g. "cricinfocricbuzz/solution-1"
            Status   status,
            long     millis,
            String   detail          // last error line(s) on failure
    ) {}

    // ── Discovery ─────────────────────────────────────────────────────────────

    /** Walk solutions/ and return every direct-child directory that owns a .git. */
    static List<Path> discoverRepos(Path solutionsDir) throws IOException {
        var repos = new ArrayList<Path>();
        try (var problems = Files.list(solutionsDir)) {
            for (var problem : problems.sorted(Comparator.naturalOrder()).toList()) {
                if (!Files.isDirectory(problem)) continue;
                try (var solutions = Files.list(problem)) {
                    for (var sol : solutions.sorted(Comparator.naturalOrder()).toList()) {
                        if (Files.isDirectory(sol.resolve(".git"))) {
                            repos.add(sol);
                        }
                    }
                }
            }
        }
        return List.copyOf(repos);
    }

    // ── Git helpers ───────────────────────────────────────────────────────────

    /** Run a git command inside {@code dir}. Returns {exitCode, output}. */
    static int[] runGit(Path dir, String... args) throws IOException, InterruptedException {
        var cmd = new ArrayList<String>();
        cmd.add("git");
        cmd.addAll(List.of(args));

        var proc = new ProcessBuilder(cmd)
                .directory(dir.toFile())
                .redirectErrorStream(true)   // merge stderr → stdout (avoids deadlock)
                .start();

        var out = new String(proc.getInputStream().readAllBytes());
        int code = proc.waitFor();
        // store output in a thread-local-ish via a tiny holder array [code, output-hash]
        // — we only need to print on failure, so stash output in a side-channel field
        LAST_OUTPUT.set(out);
        return new int[]{code};
    }

    // tiny side-channel: last git output (per-thread safe for single-threaded use)
    static final ThreadLocal<String> LAST_OUTPUT = new ThreadLocal<>();

    // ── Sync a single repo ────────────────────────────────────────────────────

    static RepoResult sync(Path repo, Path solutionsDir) {
        var label = solutionsDir.relativize(repo).toString();
        var t0    = System.currentTimeMillis();

        try {
            // Step 1: fetch --prune
            var fetchResult = runGit(repo, "fetch", "--prune");
            if (fetchResult[0] != 0) {
                var detail = lastLines(LAST_OUTPUT.get(), 3);
                return new RepoResult(label, Status.FETCH_FAILED,
                        System.currentTimeMillis() - t0, detail);
            }

            // Step 2: pull (fast-forward only — never leave repo in merge state)
            var pullResult = runGit(repo, "pull", "--ff-only");
            if (pullResult[0] != 0) {
                var detail = lastLines(LAST_OUTPUT.get(), 3);
                return new RepoResult(label, Status.PULL_FAILED,
                        System.currentTimeMillis() - t0, detail);
            }

            return new RepoResult(label, Status.OK,
                    System.currentTimeMillis() - t0, "");

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            return new RepoResult(label, Status.FETCH_FAILED,
                    System.currentTimeMillis() - t0, e.getMessage());
        }
    }

    // ── Formatting helpers ────────────────────────────────────────────────────

    static String lastLines(String text, int n) {
        if (text == null || text.isBlank()) return "";
        var lines = text.strip().lines()
                        .filter(l -> !l.isBlank())
                        .toList();
        return String.join("\n", lines.subList(Math.max(0, lines.size() - n), lines.size()));
    }

    static String fmtDuration(long millis) {
        var d = Duration.ofMillis(millis);
        return d.toSeconds() > 0
                ? "%ds".formatted(d.toSeconds())
                : "%dms".formatted(millis);
    }

    static String icon(Status s) {
        return switch (s) {
            case OK           -> "✅";
            case FETCH_FAILED -> "❌ fetch";
            case PULL_FAILED  -> "⚠️  pull ";
        };
    }

    // ── Main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws IOException {
        var repoRoot     = Path.of(System.getProperty("user.dir"));
        var solutionsDir = repoRoot.resolve("solutions");

        if (!Files.isDirectory(solutionsDir)) {
            System.err.println("❌  solutions/ directory not found.");
            System.err.println("    Run: java tools/CloneSolutions.java  first.");
            System.exit(1);
        }

        var repos   = discoverRepos(solutionsDir);
        var sep     = "━".repeat(64);
        var results = new ArrayList<RepoResult>();

        System.out.println();
        System.out.println("🐾  Kulfi — syncing " + repos.size() + " solution repos");
        System.out.println("    (git fetch --prune  →  git pull --ff-only)");
        System.out.println(sep);

        for (var repo : repos) {
            var label = solutionsDir.relativize(repo).toString();
            System.out.printf("  %-60s", label);
            System.out.flush();

            var result = sync(repo, solutionsDir);
            results.add(result);

            System.out.printf("  %s  (%s)%n", icon(result.status()), fmtDuration(result.millis()));

            if (result.status() != Status.OK && !result.detail().isBlank()) {
                result.detail().lines()
                      .forEach(l -> System.out.println("      " + l));
            }
        }

        var ok      = results.stream().filter(r -> r.status() == Status.OK).count();
        var failed  = results.size() - ok;

        System.out.println(sep);
        System.out.printf("  ✅ %d synced    ❌ %d failed%n%n", ok, failed);

        if (failed > 0) System.exit(1);
    }
}
