import java.util.*;

public class Records {
    private final Map<String,Channel> listChannels;

    public Records() {
        this.listChannels = new LinkedHashMap<>();
    }

    public void addToListChannels(Channel channel) { listChannels.put(channel.getNameChannel(), channel); }

    public List<Channel> getListChannels() { return new ArrayList<>(listChannels.values()); }

    public String getSizeOfListChannels() { return String.valueOf(getListChannels().size()); }

    public List<Channel> getListOpenChannels() {
        ArrayList<Channel> temp = new ArrayList<>();

        for(Channel c: listChannels.values()) {
            if(c.getStateChannel())
                temp.add(c);
        }

        return temp;
    }

    public String getSizeOfListOfOpenChannels() { return String.valueOf(getListOpenChannels().size()); }

    public LinkedHashMap<String,Channel> getMapChannels() { return new LinkedHashMap<>(listChannels); }

    public LinkedHashMap<String,Channel> getMapOpenChannels() {
        LinkedHashMap<String,Channel> temp = new LinkedHashMap<>();

        for(Channel c: listChannels.values()) {
            if(c.getStateChannel())
                temp.put(c.getNameChannel(),c);
        }

        return temp;
    }
}