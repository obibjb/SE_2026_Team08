package issuetracker;

public class Comment {
    public String authorId;
    public String content;
    public String createdAt;

    public Comment(String authorId, String content, String createdAt) {
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
