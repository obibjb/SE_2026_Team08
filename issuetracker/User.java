package issuetracker;

public class User {
    public String id;
    public String name;
    public String email;
    public String role; // Admin, PL, Dev, Tester

    public User(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
