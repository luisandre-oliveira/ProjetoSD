import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class ServerWorker implements Runnable {
    private final Socket clientSocket;

    ServerWorker(Socket cls) {
        this.clientSocket = cls;
    }

    @Override
    public void run() {
        int menu;
        String userName = "", userPass;
        boolean flag;

        Records records = new Records();
        Login login = new Login();

        // initialization of two super-users who are admin and one normal user with no admin privileges
        User luis = new User("luis", "1234", true);
        User jorge = new User("jorge", "1234", true);
        User comum = new User("comum", "1234", false);

        login.addUser(luis);
        login.addUser(jorge);
        login.addUser(comum);

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
                    case 1 -> {
                        flag = login.isUserAdmin(userName);
                        if(flag) { // to send state of login. There's probably a better way to do this, but I can´t find it right now
                            taggedConnection.send(0,Boolean.TRUE.toString().getBytes());

                            Channel channel = new Channel(new String(taggedConnection.receive().data));
                            records.addToListChannels(channel);
                        } else {
                            taggedConnection.send(0,Boolean.FALSE.toString().getBytes());
                        }
                    }

                    case 2 -> {
                        flag = login.isUserAdmin(userName);
                        if(flag) { // to send state of login. There's probably a better way to do this, but I can´t find it right now
                            taggedConnection.send(0,Boolean.TRUE.toString().getBytes());
                        } else {
                            taggedConnection.send(0,Boolean.FALSE.toString().getBytes());
                        }

                        //System.out.println("MENU2");

                        /* ... */
                    }

                    case 3 -> {
                        taggedConnection.send(0,records.getSizeOfListChannels().getBytes()); // send size of list so client can know what to expect

                        if(Integer.parseInt(records.getSizeOfListChannels()) > 0) { // if there are no channels
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
                                        taggedConnection.send(0,post.getUsername().getBytes()); // send username
                                        taggedConnection.send(1,post.getTimestamp().toString().getBytes()); // send timestamp
                                        taggedConnection.send(2,post.getContent().getBytes()); // send post message
                                    }
                                }
                                else {

                                }
                            }
                        }

                    }

                    // falta adicionar case 4

                }

            } while (menu != 0);

            System.out.println("User " + userName + " logged out");

            /*
            while ((line = in.readLine()) != null) {
                out.println("LOGIN");
                out.flush();
            }
            */

            clientSocket.shutdownOutput();
            clientSocket.shutdownInput();
            clientSocket.close();
        } catch (IOException e) {
            // code to handle unexpected disconnections
            try {
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
