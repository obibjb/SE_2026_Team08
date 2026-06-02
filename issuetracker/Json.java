package issuetracker;

import java.util.*;

public class Json {
    public static String quote(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    public static String issue(Issue i) {
        return "{" +
                "\"id\":" + quote(i.id) + "," +
                "\"title\":" + quote(i.title) + "," +
                "\"description\":" + quote(i.description) + "," +
                "\"priority\":" + quote(i.priority) + "," +
                "\"status\":" + quote(i.status) + "," +
                "\"assignee\":" + quote(i.assignee) + "," +
                "\"reporter\":" + quote(i.reporter) + "," +
                "\"fixer\":" + quote(i.fixer) + "," +
                "\"date\":" + quote(i.date) + "," +
                "\"comments\":" + comments(i.comments) +
                "}";
    }

    public static String comments(List<Comment> comments) {
        List<String> parts = new ArrayList<>();
        for (Comment c : comments) {
            parts.add("{" +
                    "\"authorId\":" + quote(c.authorId) + "," +
                    "\"content\":" + quote(c.content) + "," +
                    "\"createdAt\":" + quote(c.createdAt) +
                    "}");
        }
        return "[" + String.join(",", parts) + "]";
    }

    public static String user(User u) {
        return "{" +
                "\"id\":" + quote(u.id) + "," +
                "\"name\":" + quote(u.name) + "," +
                "\"email\":" + quote(u.email) + "," +
                "\"role\":" + quote(u.role) +
                "}";
    }

    public static String arrayIssues(List<Issue> issues) {
        List<String> parts = new ArrayList<>();
        for (Issue i : issues) parts.add(issue(i));
        return "[" + String.join(",", parts) + "]";
    }

    public static String arrayUsers(List<User> users) {
        List<String> parts = new ArrayList<>();
        for (User u : users) parts.add(user(u));
        return "[" + String.join(",", parts) + "]";
    }

    public static Map<String, String> parseForm(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isEmpty()) return map;
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = urlDecode(kv[0]);
            String value = kv.length > 1 ? urlDecode(kv[1]) : "";
            map.put(key, value);
        }
        return map;
    }

    public static String urlDecode(String s) {
        try { return java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }
}
