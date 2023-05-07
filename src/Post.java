import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Post {
    private final String username;
    private final String timestamp;
    private final String content;

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Post(String username, String content) {
        this.username = username;
        this.timestamp = LocalDateTime.now().format(formatter);
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getTimestamp() {
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
