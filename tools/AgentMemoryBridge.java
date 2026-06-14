import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * AgentMemoryBridge.java — tiny REST bridge for Code Puppy ↔ agentmemory.
 *
 * Run from repo root:
 *   java tools/AgentMemoryBridge.java health
 *   java tools/AgentMemoryBridge.java remember "some fact" tag1 tag2
 *   java tools/AgentMemoryBridge.java search "query text" 5
 */
public class AgentMemoryBridge {

    private static final String DEFAULT_BASE_URL =
            System.getenv().getOrDefault("AGENTMEMORY_URL", "http://localhost:3111");
    private static final String DEFAULT_SECRET = System.getenv().getOrDefault("AGENTMEMORY_SECRET", "");
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    /**
     * Calls the health endpoint and prints the raw response body.
     */
    private int health() throws IOException, InterruptedException {
        return send("GET", "/agentmemory/health", null);
    }

    /**
     * Saves one memory with optional concepts/tags.
     */
    private int remember(String content, List<String> concepts) throws IOException, InterruptedException {
        var payload = "{" +
                jsonField("content", content) + "," +
                "\"concepts\":" + jsonArray(concepts) +
                "}";
        return send("POST", "/agentmemory/remember", payload);
    }

    /**
     * Runs semantic search against stored memories.
     */
    private int search(String query, int limit) throws IOException, InterruptedException {
        var payload = "{" +
                jsonField("query", query) + "," +
                "\"limit\":" + limit +
                "}";
        return send("POST", "/agentmemory/smart-search", payload);
    }

    /**
     * Saves a repo-specific convention in a predictable format.
     */
    private int rememberRepoDecision(String repo, String decision, List<String> concepts)
            throws IOException, InterruptedException {
        var mergedConcepts = new ArrayList<String>();
        mergedConcepts.add("repo-decision");
        mergedConcepts.add(repo);
        mergedConcepts.addAll(concepts);
        return remember("[repo=" + repo + "] " + decision, mergedConcepts);
    }

    private int send(String method, String path, String jsonBody) throws IOException, InterruptedException {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(DEFAULT_BASE_URL + path))
                .timeout(TIMEOUT)
                .header("Accept", "application/json");

        if (!DEFAULT_SECRET.isBlank()) {
            builder.header("Authorization", "Bearer " + DEFAULT_SECRET);
        }

        if (jsonBody == null) {
            builder.GET();
        } else {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofString(jsonBody));
        }

        var response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("HTTP " + response.statusCode());
        System.out.println(response.body());
        return response.statusCode();
    }

    private static String jsonField(String name, String value) {
        return "\"" + escapeJson(name) + "\":\"" + escapeJson(value) + "\"";
    }

    private static String jsonArray(List<String> values) {
        return values.stream()
                .map(v -> "\"" + escapeJson(v) + "\"")
                .reduce((a, b) -> a + "," + b)
                .map(s -> "[" + s + "]")
                .orElse("[]");
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java tools/AgentMemoryBridge.java health");
        System.out.println("  java tools/AgentMemoryBridge.java remember \"content\" [concept1 concept2 ...]");
        System.out.println("  java tools/AgentMemoryBridge.java search \"query\" [limit]");
        System.out.println("  java tools/AgentMemoryBridge.java repo-remember \"repo\" \"decision\" [concept1 concept2 ...]");
        System.out.println();
        System.out.println("Env:");
        System.out.println("  AGENTMEMORY_URL     default: http://localhost:3111");
        System.out.println("  AGENTMEMORY_SECRET  optional bearer token");
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || List.of("-h", "--help", "help").contains(args[0])) {
            printUsage();
            return;
        }

        var bridge = new AgentMemoryBridge();
        var command = args[0];
        int status;

        switch (command) {
            case "health" -> status = bridge.health();
            case "remember" -> {
                if (args.length < 2) {
                    printUsage();
                    System.exit(2);
                    return;
                }
                status = bridge.remember(args[1], tail(args, 2));
            }
            case "search" -> {
                if (args.length < 2) {
                    printUsage();
                    System.exit(2);
                    return;
                }
                int limit = args.length >= 3 ? Integer.parseInt(args[2]) : 5;
                status = bridge.search(args[1], limit);
            }
            case "repo-remember" -> {
                if (args.length < 3) {
                    printUsage();
                    System.exit(2);
                    return;
                }
                status = bridge.rememberRepoDecision(args[1], args[2], tail(args, 3));
            }
            default -> {
                printUsage();
                System.exit(2);
                return;
            }
        }

        if (status >= 400) {
            System.exit(1);
        }
    }

    private static List<String> tail(String[] args, int start) {
        var out = new ArrayList<String>();
        for (int i = start; i < args.length; i++) {
            out.add(args[i]);
        }
        return List.copyOf(out);
    }
}
