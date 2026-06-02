package issuetracker;

import java.util.ArrayList;
import java.util.List;

public class Issue {
    public String id;
    public String title;
    public String description;
    public String priority;   // Blocker, Critical, Major, Minor, Trivial
    public String status;     // New, Assigned, Fixed, Resolved, Closed, Reopened
    public String assignee;   // developer id or Unassigned
    public String reporter;   // user id
    public String fixer;      // developer id or empty
    public String date;
    public List<Comment> comments = new ArrayList<>();

    public Issue(String id, String title, String description, String priority,
                 String status, String assignee, String reporter, String fixer, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignee = assignee;
        this.reporter = reporter;
        this.fixer = fixer;
        this.date = date;
    }
}
