package issuetracker;

import java.util.ArrayList;
import java.util.List;

public class Issue {
    public String id;
    public String title;
    public String description;
    public String priority;
    public String status;
    public String assignee;
    public String reporter;
    public String date;
    public String fixer;

    public List<Comment> comments = new ArrayList<>();

    public Issue() {}

    public Issue(String id, String title, String description, String priority, String status, String assignee) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignee = assignee;
    }

    public Issue(String id, String title, String description, String priority, String status, String assignee, String reporter) {
        this(id, title, description, priority, status, assignee);
        this.reporter = reporter;
    }

    public Issue(String id, String title, String description, String priority, String status, String assignee, String reporter, String date) {
        this(id, title, description, priority, status, assignee, reporter);
        this.date = date;
    }

    public Issue(String id, String title, String description, String priority, String status, String assignee, String reporter, String fixer, String date) {
        this(id, title, description, priority, status, assignee, reporter);
        this.fixer = fixer;
        this.date = date;
    }

    public Issue(String... args) {
        if (args.length > 0) this.id = args[0];
        if (args.length > 1) this.title = args[1];
        if (args.length > 2) this.description = args[2];
        if (args.length > 3) this.priority = args[3];
        if (args.length > 4) this.status = args[4];
        if (args.length > 5) this.assignee = args[5];
        if (args.length > 6) this.reporter = args[6];
        
        if (args.length == 8) {
            this.date = args[7];
        } else if (args.length >= 9) {
            this.fixer = args[7];
            this.date = args[8];
        }
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getAssignee() { return assignee; }
    public String getReporter() { return reporter; }
    public String getDate() { return date; }
    public String getFixer() { return fixer; }

    public void setStatus(String status) { this.status = status; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public void setFixer(String fixer) { this.fixer = fixer; }

    public void addComment(Comment comment) { this.comments.add(comment); }
    public List<Comment> getComments() { return comments; }
}
