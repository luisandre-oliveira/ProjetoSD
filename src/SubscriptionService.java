import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionService {
    private final Map<String, List<User>> channelSubscribers;
    private final Map<String, List<Post>> channelPosts;

    public SubscriptionService() {
        this.channelSubscribers = new HashMap<>();
        this.channelPosts = new HashMap<>();
    }

    public void subscribe(String channelName, User user) {
        List<User> subscribers = channelSubscribers.computeIfAbsent(channelName, k -> new ArrayList<>());
        subscribers.add(user);
    }

    public void unsubscribe(String channelName, User user) {
        List<User> subscribers = channelSubscribers.get(channelName);
        if (subscribers != null) {
            subscribers.remove(user);
            if (subscribers.isEmpty()) {
                channelSubscribers.remove(channelName);
            }
        }
    }

    public void publish(String channelName, Post post) {
        List<Post> posts = channelPosts.computeIfAbsent(channelName, k -> new ArrayList<>());
        posts.add(post);
    }

    public List<Post> getPosts(String channelName) {
        return channelPosts.getOrDefault(channelName, new ArrayList<>());
    }

    public List<User> getSubscribers(String channelName) {
        return channelSubscribers.getOrDefault(channelName, new ArrayList<>());
    }
}

