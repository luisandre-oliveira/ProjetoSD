import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerWorker implements Runnable {
    private final Socket clientSocket;
    private final Records records;
    private final Login login;

    ServerWorker(Socket cls, Records records, Login login) {
        this.clientSocket = cls;
        this.records = records;
        this.login = login;
    }

    @Override
    public void run() {
        int menu;
        String userName = "", userPass;
        boolean state;

        try {
            TaggedConnection taggedConnection = new TaggedConnection(clientSocket);

            boolean logged = false;

            while(!logged) {
                userName = new String(taggedConnection.receive().data);
                userPass = new String(taggedConnection.receive().data);

                if(login.checkCredentials(userName,userPass)) {
                    System.out.println("User " + userName + " logged in");
                    logged = true;
                }

                if(logged) { // to send state of login. There's probably a better way to do this, but I can´t find it right now
                    taggedConnection.send(0,Boolean.TRUE.toString().getBytes());
                } else {
                    taggedConnection.send(0,Boolean.FALSE.toString().getBytes());
                }
            }

            /* USER IS NOW LOGGED IN */

            do {
                menu = Integer.parseInt(new String(taggedConnection.receive().data)); // get the menu option from the client

                switch (menu) {
                    case 1 -> { // create a channel
                        state = login.isUserAdmin(userName);
                        if(state) { // send state of user admin status
                            taggedConnection.send(0,Boolean.TRUE.toString().getBytes());

                            Channel channel = new Channel(new String(taggedConnection.receive().data),true); // receive channel data
                            records.addToListChannels(channel);
                        } else {
                            taggedConnection.send(0,Boolean.FALSE.toString().getBytes());
                        }
                    }

                    case 2 -> { // close a channel
                        state = login.isUserAdmin(userName);

                        if(state) { // send admin status
                            taggedConnection.send(0,Boolean.TRUE.toString().getBytes()); // send positive admin status

                            String tempSizeListOpenChannels = records.getSizeOfListOfOpenChannels();
                            List<Channel> tempListOpenChannels = new ArrayList<>(records.getListOpenChannels());

                            taggedConnection.send(0,tempSizeListOpenChannels.getBytes()); // send size of list of open channels

                            if(Integer.parseInt(tempSizeListOpenChannels) > 0) { // if there are channels
                                // send name of all channels so user can choose which channel to delete

                                for(Channel channel: tempListOpenChannels) {
                                    taggedConnection.send(0,channel.getNameChannel().getBytes()); // send all channels names
                                }

                                String chosenChannel = new String(taggedConnection.receive().data); // receive specific channel name

                                for(Channel channel: tempListOpenChannels) {
                                    if(Objects.equals(channel.getNameChannel(), chosenChannel)) { // channel that user wants to check posts from
                                        channel.closeChannel();
                                    }
                                }
                            }
                        } else {
                            taggedConnection.send(0,Boolean.FALSE.toString().getBytes()); // send negative admin status
                        }
                    }

                    case 3 -> { // see posts in a channel
                        String tempSizeListChannels = records.getSizeOfListChannels();
                        List<Channel> tempListChannels = new ArrayList<>(records.getListChannels());

                        taggedConnection.send(0,tempSizeListChannels.getBytes()); // send size of list of channels

                        if(Integer.parseInt(tempSizeListChannels) > 0) { // if there are channels
                            // send name of all channels so user can choose from which to see posts
                            for(Channel channel: tempListChannels) {
                                taggedConnection.send(0,channel.getNameChannel().getBytes()); // send all channels names
                            }

                            String chosenChannel = new String(taggedConnection.receive().data); // receive specific channel name

                            for(Channel channel: tempListChannels) {
                                if(Objects.equals(channel.getNameChannel(), chosenChannel)) { // channel that user wants to check posts from
                                    taggedConnection.send(0,channel.getNumberPostsInChannel().getBytes()); // send size of list of posts so client can know what to expect

                                    for(Post post: channel.getPosts()) {
                                        taggedConnection.send(0,post.toString().getBytes()); // send post transformed into formatted string and then bytes
                                    }
                                }
                            }
                        }
                    }

                    case 4 -> { // post in a channel

                        String tempSizeListOpenChannels = records.getSizeOfListOfOpenChannels();
                        List<Channel> tempListOpenChannels = new ArrayList<>(records.getListOpenChannels());

                        taggedConnection.send(0,tempSizeListOpenChannels.getBytes()); // send size of list so client can know what to expect

                        if(Integer.parseInt(tempSizeListOpenChannels) > 0) { // if there are channels
                            // send name of all channels so user can choose from which to see posts

                            for(Channel channel: tempListOpenChannels) {
                                taggedConnection.send(0,channel.getNameChannel().getBytes()); // send all channels names
                            }

                            String chosenChannel = new String(taggedConnection.receive().data); // receive specific channel name

                            for(Channel channel: tempListOpenChannels) {
                                if(Objects.equals(channel.getNameChannel(), chosenChannel)) { // channel that user wants to check posts from
                                    String username = new String(taggedConnection.receive().data);
                                    String message = new String(taggedConnection.receive().data);

                                    Post post = new Post(username,message);
                                    channel.addPost(post); //TODO
                                }
                            }
                        }
                    }

                    case 5 -> { // get a list of posts from various channels

                        String tempSizeListChannels = records.getSizeOfListChannels();
                        List<Channel> tempListChannels = new ArrayList<>(records.getListChannels());

                        taggedConnection.send(0,tempSizeListChannels.getBytes()); // send size of list

                        if(Integer.parseInt(tempSizeListChannels) > 0) { // if there are channels
                            // send name of all channels so user can choose from which to see posts
                            for(Channel channel: tempListChannels) {
                                taggedConnection.send(0,channel.getNameChannel().getBytes()); // send all channels names
                            }

                            for(Channel channel: tempListChannels) {
                                taggedConnection.send(0,channel.getNumberPostsInChannel().getBytes()); // send size of list of posts so client can know what to expect

                                for(Post post: channel.getPosts()) {
                                    taggedConnection.send(0,post.toString().getBytes()); // send post transformed into formatted string and then bytes
                                }
                            }
                        }
                    }
                }

            } while (menu != 0);

            System.out.println("User " + userName + " logged out");

            taggedConnection.close();
            clientSocket.close();
        } catch (IOException e) {
            // code to handle unexpected disconnections
            try {
                clientSocket.close();
                System.out.println("User " + userName + " crashed and is now logged out");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
