import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Client {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", PORT);
        TaggedConnection taggedConnection = new TaggedConnection(socket);
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in)); // read from keyboard

        String userName = "", userPass; // user information
        boolean userLoginStatus = false; // true if logged
        boolean userAdminStatus = false; // true if admin
        String state, menu;

        while(!userLoginStatus) {
            // Read the socket for the prompt, then reading the username from keyboard, and finally sending it
            System.out.println("Enter username: "); //Enter username prompt
            userName = stdin.readLine(); // User writes their username

            taggedConnection.send(0,userName.getBytes());

            // Read the socket for the prompt, then reading the password from keyboard, and finally sending it
            System.out.println("Enter password: "); //Enter password prompt
            userPass = stdin.readLine(); // User writes their password

            taggedConnection.send(0,userPass.getBytes());

            // Read the socket for to see if logged or not
            state = new String(taggedConnection.receive().data);

            if(Objects.equals(state, Boolean.TRUE.toString())) {
                userLoginStatus = true;
                System.out.println("\n--SUCCESS: login successful--");
            } else {
                System.out.println("\n--WARNING: login unsuccessful--");
            }
        }

        /* USER IS NOW LOGGED IN */

        do {
            System.out.println("\n--WARNING: ALWAYS CHOOSE THE NUMBER REFERRING TO THE OPTION YOU WANT!!--");
            System.out.println("Welcome to main menu!!");
            System.out.println("1 - (ADMIN) Create a channel.");
            System.out.println("2 - (ADMIN) Close a channel.");
            System.out.println("3 - See posts in a channel.");
            System.out.println("4 - Post in a channel.");
            System.out.println("5 - Get a list of posts from various channels.");
            System.out.println("0 - Exit the program.");
            System.out.println("\nWhat do you wish to do?");

            menu = stdin.readLine(); // get the menu option from the user
            taggedConnection.send(0,menu.getBytes()); // send the option so the server can decide if it can advance or not

            switch (Integer.parseInt(menu)) {
                case 1 -> {
                    System.out.println("\nYou chose creating a channel.");
                    state = new String(taggedConnection.receive().data); // receive state of user admin status

                    if(userAdminStatus || Objects.equals(state, Boolean.TRUE.toString())) {
                        userAdminStatus = true;

                        System.out.println("What should the name of the channel be?");
                        String name = stdin.readLine(); // get from keyboard name of channel

                        taggedConnection.send(0,name.getBytes()); // send channel data
                    } else {
                        System.out.println("\n--ERROR: user doesn´t have admin status--\n");
                    }
                }

                case 2 -> {
                    System.out.println("\nYou chose closing a channel.");
                    state = new String(taggedConnection.receive().data); // receive admin status

                    if(userAdminStatus || Objects.equals(state, Boolean.TRUE.toString())) {
                        userAdminStatus = true;
                    } else {
                        System.out.println("\n--ERROR: user doesn´t have admin status--\n");
                    }

                    int sizeListOpenChannels = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list of open channels
                    System.out.println("There are " + sizeListOpenChannels + " open channels.");

                    if(sizeListOpenChannels > 0) {
                        ArrayList<String> tempListOpenChannels = new ArrayList<>();

                        for (int counter = 0; counter < sizeListOpenChannels; counter++) {
                            tempListOpenChannels.add(new String(taggedConnection.receive().data)); // receive all channels names
                            System.out.println(counter + " - " + tempListOpenChannels.get(counter));
                        }

                        System.out.println("What channel to you want to close?");
                        int chosenChannel = Integer.parseInt(stdin.readLine());

                        taggedConnection.send(0, tempListOpenChannels.get(chosenChannel).getBytes()); // send specific channel name
                    }
                }

                case 3 -> {
                    System.out.println("\nYou chose seeing posts in a channel.");

                    int sizeListChannels = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list of channels
                    System.out.println("There are " + sizeListChannels + " open channels.");

                    if(sizeListChannels > 0) { // go through every channel
                        ArrayList<String> tempListNameChannels = new ArrayList<>();

                        for (int counter = 0; counter < sizeListChannels; counter++) {
                            tempListNameChannels.add(new String(taggedConnection.receive().data)); // receive all channels names
                            System.out.println(counter + " - " + tempListNameChannels.get(counter)); // print all the channels names
                        }

                        System.out.println("What channel to you want to see?");
                        int chosenChannel = Integer.parseInt(stdin.readLine()); // read from keyboard the id of the channel

                        taggedConnection.send(0,tempListNameChannels.get(chosenChannel).getBytes()); // send specific channel id in list

                        int sizeListPostsInChannel = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list of posts

                        String message;

                        if(sizeListPostsInChannel > 0) {
                            for (int counter = 0; counter < sizeListPostsInChannel; counter++) {
                                message = new String(taggedConnection.receive().data); // receive post transformed into formatted string and then bytes
                                System.out.println(message);
                            }
                        } else {
                            System.out.println("\n--WARNING: No posts have been sent in channel: " + tempListNameChannels.get(chosenChannel) + " --");
                        }
                    } else {
                        System.out.println("\n--WARNING: There are no open channels--");
                    }
                }

                case 4 -> {
                    System.out.println("\nYou chose posting to a channel.");

                    int sizeListChannels = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list
                    System.out.println("There are " + sizeListChannels + " open channels.");

                    if(sizeListChannels > 0) {
                        ArrayList<String> tempListChannels = new ArrayList<>();

                        for (int counter = 0; counter < sizeListChannels; counter++) {
                            tempListChannels.add(new String(taggedConnection.receive().data)); // receive all channels names
                            System.out.println(counter + " - " + tempListChannels.get(counter));
                        }

                        System.out.println("\nWhat channel to you want to post a message?");
                        int chosenChannel = Integer.parseInt(stdin.readLine());

                        taggedConnection.send(0,tempListChannels.get(chosenChannel).getBytes()); // send specific channel name

                        System.out.println("What is the message for the post?");
                        String message = stdin.readLine();
                        taggedConnection.send(0,userName.getBytes());
                        taggedConnection.send(1,message.getBytes());
                    } else {
                        System.out.println("\n--WARNING: There are no open channels--");
                    }
                }

                case 5 -> {
                    System.out.println("\nYou chose seeing posts from various channels.");

                    int tempSizeListChannels = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list of channels
                    System.out.println("There are " + tempSizeListChannels + " channels available.");

                    if(tempSizeListChannels > 0) { // go through every channel
                        ArrayList<String> tempListNameChannels = new ArrayList<>();

                        for (int counter = 0; counter < tempSizeListChannels; counter++) {
                            tempListNameChannels.add(new String(taggedConnection.receive().data)); // receive all channels names
                        }

                        for (int channelCounter = 0; channelCounter < tempSizeListChannels; channelCounter++) { // go through every channel
                            int tempSizeListPostsInChannel = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list of posts in channel

                            String message;

                            System.out.println("\n" + tempListNameChannels.get(channelCounter));

                            if(tempSizeListPostsInChannel > 0) {
                                for (int postCounter = 0; postCounter < tempSizeListPostsInChannel; postCounter++) { // go through every post in channel
                                    message = new String(taggedConnection.receive().data); // receive post transformed into formatted string and then bytes
                                    System.out.println(message);
                                }
                            } else {
                                System.out.println("--No posts have been sent in this channel.--");
                            }
                        }

                    } else {
                        System.out.println("\n--WARNING: There are no open channels--");
                    }
                }

                case 0 -> System.out.println("\n--WARNING: You chose exiting the program.--");

                default -> System.out.println("\n--WARNING: You chose poorly... Please try again.");
            }
        } while(Integer.parseInt(menu) != 0);

        /* USER HAS CHOSEN TO EXIT */

        System.out.println("\nCLOSING SOCKET...");
        taggedConnection.close();
        socket.close();
        System.out.println("SOCKET CLOSED.");
    }
}
