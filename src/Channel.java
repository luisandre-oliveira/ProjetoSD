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

    public void addPost(Post post) { posts.add(post); }

    public List<Post> getPosts() { return this.posts; }

    public String getNameChannel() { return name; }

    public String getNumberPostsInChannel() { return String.valueOf(getPosts().size()); }
}