import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final String name;
    private boolean state; // whether the channel is open or closed, true = open && false = closed
    private final List<Post> posts;


    public Channel(String name, boolean state) {
        this.name = name;
        this.state = state;
        this.posts = new ArrayList<>();
    }

    public void addPost(Post post) { this.posts.add(post); }

    public List<Post> getPosts() { return this.posts; }

    public Post getLastPost() { return posts.get(posts.size() - 1); }

    public String getNameChannel() { return this.name; }

    public String getNumberPostsInChannel() { return String.valueOf(this.getPosts().size()); }

    public boolean getStateChannel() { return this.state; }

    public void closeChannel() { this.state = false; }
}