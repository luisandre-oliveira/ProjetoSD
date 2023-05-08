import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final String name;
    private boolean state;
    private final List<Post> posts;


    public Channel(String name, boolean state) {
        this.name = name;
        this.state = state;
        this.posts = new ArrayList<>();
    }

    public void addPost(Post post) { this.posts.add(post); }

    public List<Post> getPosts() { return this.posts; }

    public String getNameChannel() { return this.name; }

    public String getNumberPostsInChannel() { return String.valueOf(this.getPosts().size()); }

    public boolean getStateChannel() { return this.state; }

    public void closeChannel() { this.state = false; }
}