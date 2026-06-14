import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DownloadVideos.java — Java 21 utility to download all video links from solutions.md.
 *
 * Behavior:
 * - Parses the markdown table in solutions.md
 * - Extracts links from the "Videos" column
 * - Creates directory structure similar to solutions/:
 *     videos/<problem-slug>/
 * - Downloads each video with yt-dlp in highest quality
 *
 * Requirements:
 * - Java 21+
 * - yt-dlp on PATH
 * - ffmpeg on PATH (for bestvideo+bestaudio merge)
 *
 * Usage (from repo root):
 *   java tools/DownloadVideos.java
 *
 * Optional args:
 *   --dry-run     Print what would be downloaded, do not download
 *   --limit=N     Download only first N problem entries (useful for testing)
 */
public class DownloadVideos {

    record VideoLink(String label, String url) {}

    record ProblemVideos(String name, String slug, List<VideoLink> videos) {}

    private static final Pattern LINK = Pattern.compile("\\[([^\\]]+)]\\((https?://[^)\\s]+)\\)");
    private static final Pattern NON_SLUG_CHAR = Pattern.compile("[^\\w\\s-]");
    private static final Pattern WHITESPACE_OR_SLASH = Pattern.compile("[\\s/]+");
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    static String slugify(String name) {
        var s = NON_SLUG_CHAR.matcher(name.toLowerCase()).replaceAll("");
        s = WHITESPACE_OR_SLASH.matcher(s.strip()).replaceAll("-");
        s = MULTI_HYPHEN.matcher(s).replaceAll("-");
        return s.replaceAll("^-+|-+$", "");
    }

    static boolean isHeaderOrSeparator(String firstCell) {
        var c = firstCell.strip().toLowerCase();
        return c.isBlank() || c.startsWith("question") || c.contains(":-");
    }

    static String[] parseRowCells(String line) {
        var raw = line.strip();
        if (!raw.startsWith("|")) return new String[0];

        var parts = raw.split("\\|");
        var cells = new ArrayList<String>();
        for (var p : parts) {
            var cell = p.strip();
            if (!cell.isEmpty()) {
                cells.add(cell);
            }
        }
        return cells.toArray(String[]::new);
    }

    static List<VideoLink> extractYoutubeLinks(String cell) {
        var out = new ArrayList<VideoLink>();
        var seen = new LinkedHashSet<String>();
        var m = LINK.matcher(cell);

        while (m.find()) {
            var label = m.group(1).strip();
            var url = m.group(2).strip();
            var lower = url.toLowerCase();

            if (!(lower.contains("youtube.com") || lower.contains("youtu.be"))) {
                continue;
            }
            if (seen.add(url)) {
                out.add(new VideoLink(label, url));
            }
        }
        return List.copyOf(out);
    }

    static List<ProblemVideos> parseProblemsWithVideos(Path solutionsMd) throws IOException {
        var lines = Files.readAllLines(solutionsMd);
        var out = new ArrayList<ProblemVideos>();

        for (var line : lines) {
            var cells = parseRowCells(line);
            if (cells.length < 3) continue;

            var questionCell = LINK.matcher(cells[0]).replaceAll("$1").strip();
            if (isHeaderOrSeparator(questionCell)) continue;

            var videosCell = cells[2];
            var videos = extractYoutubeLinks(videosCell);
            var slug = slugify(questionCell);

            out.add(new ProblemVideos(questionCell, slug, videos));
        }
        return List.copyOf(out);
    }

    static boolean hasExistingDownload(Path problemDir, int index) throws IOException {
        var prefix = "video-" + index + "-";
        try (var stream = Files.list(problemDir)) {
            return stream.anyMatch(p -> p.getFileName().toString().startsWith(prefix));
        }
    }

    static int downloadOne(Path problemDir, int index, String url, boolean dryRun)
            throws IOException, InterruptedException {
        var outputTemplate = problemDir.resolve("video-" + index + "-%(title).120B.%(ext)s").toString();

        var cmd = List.of(
                "yt-dlp",
                "--yes-playlist",
                "-f", "bestvideo*+bestaudio/best",
                "--merge-output-format", "mp4",
                "-o", outputTemplate,
                url
        );

        if (dryRun) {
            System.out.println("      [DRY] " + String.join(" ", cmd));
            return 0;
        }

        var proc = new ProcessBuilder(cmd)
                .redirectErrorStream(true)
                .start();

        var output = new String(proc.getInputStream().readAllBytes());
        var exit = proc.waitFor();

        if (exit != 0) {
            System.out.println("      ❌ yt-dlp failed");
            output.lines()
                    .filter(l -> !l.isBlank())
                    .skip(Math.max(0, output.lines().filter(l -> !l.isBlank()).count() - 6))
                    .forEach(l -> System.out.println("         " + l));
        }
        return exit;
    }

    static boolean commandExists(String command) {
        try {
            var proc = new ProcessBuilder("bash", "-lc", "command -v " + command)
                    .redirectErrorStream(true)
                    .start();
            return proc.waitFor() == 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        boolean dryRun = false;
        int limit = Integer.MAX_VALUE;

        for (var arg : args) {
            if ("--dry-run".equals(arg)) {
                dryRun = true;
            } else if (arg.startsWith("--limit=")) {
                limit = Integer.parseInt(arg.substring("--limit=".length()));
            }
        }

        if (!dryRun && !commandExists("yt-dlp")) {
            System.err.println("❌ yt-dlp not found on PATH. Install it first.");
            System.exit(1);
        }

        if (!dryRun && !commandExists("ffmpeg")) {
            System.err.println("❌ ffmpeg not found on PATH. Install it first.");
            System.exit(1);
        }

        var repoRoot = Path.of(System.getProperty("user.dir"));
        var solutionsMd = repoRoot.resolve("solutions.md");
        var videosRoot = repoRoot.resolve("videos");

        if (!Files.exists(solutionsMd)) {
            System.err.println("❌ solutions.md not found in repo root.");
            System.exit(1);
        }

        var problems = parseProblemsWithVideos(solutionsMd);
        Files.createDirectories(videosRoot);

        int scanned = 0;
        int downloadAttempts = 0;
        int downloaded = 0;
        int failed = 0;

        System.out.println("\n🐾 DownloadVideos — pull videos from solutions.md");
        System.out.println("   output: " + videosRoot);
        if (dryRun) System.out.println("   mode  : DRY RUN");
        System.out.println();

        for (var p : problems) {
            if (scanned >= limit) break;
            scanned++;

            var problemDir = videosRoot.resolve(p.slug());
            Files.createDirectories(problemDir);

            System.out.println("📁 " + p.name() + "  -> videos/" + p.slug());
            if (p.videos().isEmpty()) {
                System.out.println("      (no YouTube links)");
                continue;
            }

            for (int i = 0; i < p.videos().size(); i++) {
                int idx = i + 1;
                var v = p.videos().get(i);

                if (hasExistingDownload(problemDir, idx)) {
                    System.out.println("      ⏭️  video-" + idx + " already exists, skipping");
                    continue;
                }

                System.out.println("      🔽 [" + v.label() + "] " + v.url());
                downloadAttempts++;

                int exit = downloadOne(problemDir, idx, v.url(), dryRun);
                if (exit == 0) downloaded++;
                else failed++;
            }
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Scanned problems : " + scanned);
        System.out.println("Download attempts: " + downloadAttempts);
        System.out.println("Downloaded ok    : " + downloaded);
        System.out.println("Failed           : " + failed);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (failed > 0) System.exit(1);
    }
}
