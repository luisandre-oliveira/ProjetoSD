import java.time.LocalDateTime;

public class Post {
    private final String username;
    private final LocalDateTime timestamp;
    private final String content;

    public Post(String username, String content) {
        this.username = username;
        this.timestamp = LocalDateTime.now();
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + username + ": " + content;
    }
}
