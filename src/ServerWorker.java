import java.io.IOException;
import java.net.Socket;
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
        boolean flag;

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
                        flag = login.isUserAdmin(userName);
                        if(flag) { // to send state of login. There's probably a better way to do this, but I can´t find it right now
                            taggedConnection.send(0,Boolean.TRUE.toString().getBytes());

                            Channel channel = new Channel(new String(taggedConnection.receive().data),true);
                            records.addToListChannels(channel);
                        } else {
                            taggedConnection.send(0,Boolean.FALSE.toString().getBytes());
                        }
                    }

                    case 2 -> { // close a channel
                        flag = login.isUserAdmin(userName);
                        if(flag) { // to send state of login. There's probably a better way to do this, but I can´t find it right now
                            taggedConnection.send(0,Boolean.TRUE.toString().getBytes());

                            taggedConnection.send(0,records.getSizeOfListOfOpenChannels().getBytes()); // send size of list of open channels

                            if(Integer.parseInt(records.getSizeOfListOfOpenChannels()) > 0) { // if there are channels
                                // send name of all channels so user can choose from which to see posts
                                int tagNumber = 1;
                                for(Channel channel: records.getListOpenChannels()) {
                                    taggedConnection.send(tagNumber,channel.getNameChannel().getBytes()); // send all channels names
                                    tagNumber++;
                                }

                                String chosenChannel = new String(taggedConnection.receive().data); // receive specific channel name

                                for(Channel channel: records.getListOpenChannels()) {
                                    if(Objects.equals(channel.getNameChannel(), chosenChannel)) { // channel that user wants to check posts from
                                        channel.closeChannel();
                                    }
                                }
                            }
                        } else {
                            taggedConnection.send(0,Boolean.FALSE.toString().getBytes());
                        }
                    }

                    case 3 -> { // see posts in a channel
                        taggedConnection.send(0,records.getSizeOfListChannels().getBytes()); // send size of list so client can know what to expect

                        if(Integer.parseInt(records.getSizeOfListChannels()) > 0) { // if there are channels
                            // send name of all channels so user can choose from which to see posts
                            int tagNumber = 1;
                            for(Channel channel: records.getListChannels()) {
                                taggedConnection.send(tagNumber,channel.getNameChannel().getBytes()); // send all channels names
                                tagNumber++;
                            }

                            String chosenChannel = new String(taggedConnection.receive().data); // receive specific channel name

                            for(Channel channel: records.getListChannels()) {
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
                        taggedConnection.send(0,records.getSizeOfListOfOpenChannels().getBytes()); // send size of list so client can know what to expect

                        if(Integer.parseInt(records.getSizeOfListOfOpenChannels()) > 0) { // if there are channels
                            // send name of all channels so user can choose from which to see posts
                            int tagNumber = 1;
                            for(Channel channel: records.getListOpenChannels()) {
                                taggedConnection.send(tagNumber,channel.getNameChannel().getBytes()); // send all channels names
                                tagNumber++;
                            }

                            String chosenChannel = new String(taggedConnection.receive().data); // receive specific channel name

                            for(Channel channel: records.getListOpenChannels()) {
                                if(Objects.equals(channel.getNameChannel(), chosenChannel)) { // channel that user wants to check posts from
                                    String username = new String(taggedConnection.receive().data);
                                    String message = new String(taggedConnection.receive().data);

                                    Post post = new Post(username,message);
                                    channel.addPost(post);
                                }
                            }
                        }
                    }

                    case 5 -> { // get a list of posts from various channels
                        taggedConnection.send(0,records.getSizeOfListChannels().getBytes()); // send size of list

                        if(Integer.parseInt(records.getSizeOfListChannels()) > 0) { // if there are channels
                            // send name of all channels so user can choose from which to see posts
                            for(Channel channel: records.getListChannels()) {
                                taggedConnection.send(0,channel.getNameChannel().getBytes()); // send all channels names
                            }

                            for(Channel channel: records.getListChannels()) {
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

    // TODO: convert some of the repeated actions into functions I can call

}
