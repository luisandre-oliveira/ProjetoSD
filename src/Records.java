import java.util.ArrayList;
import java.util.List;

public class Records {
    private final List<Channel> listChannels;

    public Records() {
        this.listChannels = new ArrayList<>();
    }

    public void addToListChannels(Channel channel) { listChannels.add(channel); }

    public List<Channel> getListChannels() { return new ArrayList<>(listChannels); }

    public String getSizeOfListChannels() { return String.valueOf(getListChannels().size()); }

    public List<Channel> getListOpenChannels() {
        ArrayList<Channel> temp = new ArrayList<>();

        for(Channel c: listChannels) {
            if(c.getStateChannel())
                temp.add(c);
        }

        return temp;
    }

    public String getSizeOfListOfOpenChannels() { return String.valueOf(getListOpenChannels().size()); }


}