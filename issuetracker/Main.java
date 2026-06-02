package issuetracker;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    private static final IssueTrackerService service = new IssueTrackerService();
    private static final Path PUBLIC_DIR = Path.of("public");

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/issues", Main::handleIssues);
        server.createContext("/api/users", Main::handleUsers);
        server.createContext("/api/statistics", Main::handleStatistics);
        server.createContext("/api/assign", Main::handleAssign);
        server.createContext("/api/status", Main::handleStatus);
        server.createContext("/api/comment", Main::handleComment);
        server.createContext("/api/recommend", Main::handleRecommend);
        server.createContext("/", Main::handleStaticFiles);
        server.start();
        System.out.println("Issue Tracker running at http://localhost:8080/");
    }

    private static void handleIssues(HttpExchange ex) throws IOException {
        try {
            if (ex.getRequestMethod().equals("GET")) {
                Map<String, String> q = parseQuery(ex.getRequestURI().getRawQuery());
                sendJson(ex, Json.arrayIssues(service.getIssues(q.get("status"), q.get("priority"), q.get("assignee"), q.get("keyword"))));
            } else if (ex.getRequestMethod().equals("POST")) {
                Map<String, String> form = Json.parseForm(readBody(ex));
                sendJson(ex, Json.issue(service.createIssue(form.get("title"), form.get("description"), form.get("priority"), form.get("reporter"))));
            }
        } catch (Exception e) { sendError(ex, e); }
    }

    private static void handleUsers(HttpExchange ex) throws IOException {
        try {
            if (ex.getRequestMethod().equals("GET")) {
                sendJson(ex, Json.arrayUsers(service.getUsers()));
            } else if (ex.getRequestMethod().equals("POST")) {
                Map<String, String> f = Json.parseForm(readBody(ex));
                sendJson(ex, Json.user(service.createUser(f.get("id"), f.get("name"), f.get("email"), f.get("role"))));
            }
        } catch (Exception e) { sendError(ex, e); }
    }

    private static void handleStatistics(HttpExchange ex) throws IOException {
        sendJson(ex, service.statisticsJson());
    }

    private static void handleAssign(HttpExchange ex) throws IOException {
        try {
            Map<String, String> f = Json.parseForm(readBody(ex));
            sendJson(ex, Json.issue(service.assignIssue(f.get("issueId"), f.get("devId"), f.getOrDefault("plId", "pl1"), f.get("comment"))));
        } catch (Exception e) { sendError(ex, e); }
    }

    private static void handleStatus(HttpExchange ex) throws IOException {
        try {
            Map<String, String> f = Json.parseForm(readBody(ex));
            sendJson(ex, Json.issue(service.changeStatus(f.get("issueId"), f.get("status"), f.getOrDefault("userId", "dev1"), f.get("comment"))));
        } catch (Exception e) { sendError(ex, e); }
    }

    private static void handleComment(HttpExchange ex) throws IOException {
        try {
            Map<String, String> f = Json.parseForm(readBody(ex));
            sendJson(ex, Json.issue(service.addComment(f.get("issueId"), f.getOrDefault("authorId", "tester1"), f.get("content"))));
        } catch (Exception e) { sendError(ex, e); }
    }

    private static void handleRecommend(HttpExchange ex) throws IOException {
        try {
            Map<String, String> q = parseQuery(ex.getRequestURI().getRawQuery());
            sendJson(ex, "{\"recommendation\":" + Json.quote(service.recommendAssignee(q.get("issueId"))) + "}");
        } catch (Exception e) { sendError(ex, e); }
    }

    private static void handleStaticFiles(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath().equals("/") ? "/index.html" : ex.getRequestURI().getPath();
        Path file = PUBLIC_DIR.resolve(path.substring(1)).normalize();
        if (!file.startsWith(PUBLIC_DIR) || !Files.exists(file)) {
            ex.sendResponseHeaders(404, -1);
            return;
        }
        byte[] data = Files.readAllBytes(file);
        ex.getResponseHeaders().set("Content-Type", contentType(file.toString()));
        ex.sendResponseHeaders(200, data.length);
        ex.getResponseBody().write(data);
        ex.close();
    }

    private static String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static Map<String, String> parseQuery(String raw) {
        return Json.parseForm(raw == null ? "" : raw);
    }

    private static void sendJson(HttpExchange ex, String json) throws IOException {
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(200, data.length);
        ex.getResponseBody().write(data);
        ex.close();
    }

    private static void sendError(HttpExchange ex, Exception e) throws IOException {
        sendJson(ex, "{\"error\":" + Json.quote(e.getMessage()) + "}");
    }

    private static String contentType(String file) {
        if (file.endsWith(".css")) return "text/css; charset=UTF-8";
        if (file.endsWith(".js")) return "application/javascript; charset=UTF-8";
        return "text/html; charset=UTF-8";
    }
}
