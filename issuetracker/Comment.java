package issuetracker;

public class Comment implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    public String authorId;
    public String content;
    public String createdAt;

    public Comment(String authorId, String content, String createdAt) {
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
