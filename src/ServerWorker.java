import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

        try {
            TaggedConnection taggedConnection = new TaggedConnection(clientSocket);

            boolean logged = false;

            while(!logged) {
                userName = new String(taggedConnection.receive().data); // receive username
                userPass = new String(taggedConnection.receive().data); // receive user's password

                if(login.checkCredentials(userName,userPass)) {
                    System.out.println("User " + userName + " logged in");
                    logged = true;
                }

                if(logged) {
                    taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_OK).getBytes()); // send log success
                } else {
                    taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_FORBIDDEN).getBytes()); // send log fail
                }
            }

            /* USER IS NOW LOGGED IN */

            do {
                menu = Integer.parseInt(new String(taggedConnection.receive().data)); // receive the menu option from the client

                switch (menu) {
                    case 1 -> { // create a channel
                        boolean state = login.isUserAdmin(userName);

                        if(state) {
                            taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_ACCEPTED).getBytes()); // send positive admin status

                            Channel channel = new Channel(new String(taggedConnection.receive().data),true); // receive channel name

                            if(!records.getMapChannels().containsKey(channel.getNameChannel())) { // channel doesn't exist
                                records.addToListChannels(channel);
                                taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_CREATED).getBytes()); // Send channel create success
                            } else { // channel already exists
                                taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_CONFLICT).getBytes()); // Send channel create fail
                            }

                        } else {
                            taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_UNAUTHORIZED).getBytes()); // send negative admin status
                        }
                    }

                    case 2 -> { // close a channel
                        boolean state = login.isUserAdmin(userName);

                        if(state) {
                            taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_ACCEPTED).getBytes()); // send positive admin status

                            String tempSizeListOpenChannels = records.getSizeOfListOfOpenChannels();
                            List<Channel> tempListOpenChannels = new ArrayList<>(records.getListOpenChannels());

                            taggedConnection.send(0,tempSizeListOpenChannels.getBytes()); // send size of list of open channels

                            if(Integer.parseInt(tempSizeListOpenChannels) > 0) { // if there are channels
                                /* send name of all channels so user can choose which channel to delete */
                                for(Channel channel: tempListOpenChannels) {
                                    taggedConnection.send(0,channel.getNameChannel().getBytes()); // send all channels names
                                }

                                String chosenChannelName = new String(taggedConnection.receive().data); // receive specific channel name

                                boolean channelExists = false;

                                for(Channel channel: tempListOpenChannels) {
                                    if(Objects.equals(channel.getNameChannel(), chosenChannelName)) { // channel that user wants to close
                                        channel.closeChannel();
                                        channelExists = true;
                                        taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_OK).getBytes()); // send delete success
                                    }
                                }

                                if(!channelExists) {
                                    taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_NOT_FOUND).getBytes()); // send delete success
                                }
                            }
                        } else {
                            taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_UNAUTHORIZED).getBytes()); // send negative admin status
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

                            String chosenChannelName = new String(taggedConnection.receive().data); // receive specific channel name

                            for(Channel channel: tempListChannels) {
                                if(Objects.equals(channel.getNameChannel(), chosenChannelName)) { // channel that user wants to check posts from
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

                        taggedConnection.send(0,tempSizeListOpenChannels.getBytes()); // send size of list of open channels

                        if(Integer.parseInt(tempSizeListOpenChannels) > 0) { // if there are channels
                            // send name of all channels so user can choose where to post

                            for(Channel channel: tempListOpenChannels) {
                                taggedConnection.send(0,channel.getNameChannel().getBytes()); // send all open channels names
                            }

                            String chosenChannelName = new String(taggedConnection.receive().data); // receive specific channel name

                            for(Channel channel: tempListOpenChannels) {
                                if(Objects.equals(channel.getNameChannel(), chosenChannelName)) { // channel that user wants to check posts from
                                    String username = new String(taggedConnection.receive().data); // receive username
                                    String message = new String(taggedConnection.receive().data); // receive message

                                    Post post = new Post(username,message);
                                    channel.addPost(post);

                                    taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_CREATED).getBytes()); // send post success
                                }
                            }
                        }
                    }

                    case 5 -> { // get a list of posts from various channels
                        String tempSizeListChannels = records.getSizeOfListChannels();
                        List<Channel> tempListChannels = new ArrayList<>(records.getListChannels());

                        taggedConnection.send(0,tempSizeListChannels.getBytes()); // send size of list of channels

                        if(Integer.parseInt(tempSizeListChannels) > 0) { // if there are channels
                            // send name of all channels so user can choose from which to see posts
                            for(Channel channel: tempListChannels) {
                                taggedConnection.send(0,channel.getNameChannel().getBytes()); // send all channels names
                            }

                            for(Channel channel: tempListChannels) {
                                taggedConnection.send(0,channel.getNumberPostsInChannel().getBytes()); // send size of list of posts per channel

                                for(Post post: channel.getPosts()) {
                                    taggedConnection.send(0,post.toString().getBytes()); // send post transformed into formatted string and then bytes
                                }
                            }
                        }
                    }

                    case 6 -> { // wait for a new post in a channel
                        String tempSizeListOpenChannels = records.getSizeOfListOfOpenChannels();
                        LinkedHashMap<String,Channel> tempMapChannels = new LinkedHashMap<>(records.getMapOpenChannels());

                        taggedConnection.send(0,tempSizeListOpenChannels.getBytes()); // send size of list of channels

                        if(Integer.parseInt(tempSizeListOpenChannels) > 0) { // if there are channels
                            // send name of all channels so user can choose from which to wait for post
                            for(String channelName: tempMapChannels.keySet()) {
                                taggedConnection.send(0,channelName.getBytes()); // send all channels names
                            }

                            String chosenChannelName = new String(taggedConnection.receive().data); // receive specific channel name
                            Channel tempChannel = tempMapChannels.get(chosenChannelName);

                            int tempNumberPostsBefore = tempChannel.getPosts().size();
                            int tempNumberPostsAfter;

                            taggedConnection.send(0, String.valueOf(tempNumberPostsBefore).getBytes()); // send number posts before
                            String newState = String.valueOf(HttpsURLConnection.HTTP_NOT_FOUND); // send state

                            while(newState.equals(String.valueOf(HttpsURLConnection.HTTP_NOT_FOUND))) {
                                tempNumberPostsAfter = records.getMapChannels().get(chosenChannelName).getPosts().size();
                                taggedConnection.send(0, String.valueOf(tempNumberPostsAfter).getBytes()); // send number posts after
                                newState = new String(taggedConnection.receive().data); // receive state of new post
                            }

                            Post newPost = records.getMapChannels().get(chosenChannelName).getLastPost();
                            taggedConnection.send(0,newPost.toString().getBytes()); // send post transformed into formatted string and then bytes
                        }
                    }
                }

            } while (menu != 0);

            System.out.println("User " + userName + " logged out");

            taggedConnection.close();
            clientSocket.close();
        } catch (IOException e) { // general exception at the start
            // code to handle unexpected disconnections
            try {
                clientSocket.close();
                System.out.println("User " + userName + " crashed and is now logged out");
            } catch (IOException ex) { // in case of user crash and can't close socket
                throw new RuntimeException(ex);
            }
        }
    }
}
