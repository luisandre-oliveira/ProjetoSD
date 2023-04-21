import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final String name;
    private final List<String> messages;

    public Channel(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
    }

    public synchronized void addMessage(String message) {
        messages.add(message);
    }

    public synchronized List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public String getName() {
        return name;
    }
}

