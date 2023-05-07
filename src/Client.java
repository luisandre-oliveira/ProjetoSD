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
            System.out.println("\nWelcome to main menu!!");
            System.out.println("1 - (ADMIN) Create a channel.");
            System.out.println("2 - (ADMIN) Close a channel.");
            System.out.println("3 - See posts in a channel.");
            System.out.println("4 - Post in a channel.");
            System.out.println("5 - Get a list of posts from various channels.");
            System.out.println("0 - Exit the program."); // TODO: maybe later add the functionality to just logout instead of exit
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
                        String name = stdin.readLine();

                        taggedConnection.send(0,name.getBytes());
                    } else {
                        System.out.println("\n--ERROR: user doesn´t have admin status--\n");
                    }
                }

                case 2 -> {
                    System.out.println("\nYou chose closing a channel.");
                    state = new String(taggedConnection.receive().data);

                    if(userAdminStatus || Objects.equals(state, Boolean.TRUE.toString())) {
                        userAdminStatus = true;
                    } else {
                        System.out.println("\n--ERROR: user doesn´t have admin status--\n");
                    }

                    //TODO: client delete channel
                }

                case 3 -> {
                    System.out.println("\nYou chose seeing posts in a channel.");

                    int sizeListChannels = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list
                    System.out.println("There are " + sizeListChannels + " open channels.");

                    if(sizeListChannels > 0) {
                        ArrayList<String> tempListChannels = new ArrayList<>();

                        for (int counter = 0; counter < sizeListChannels; counter++) {
                            tempListChannels.add(new String(taggedConnection.receive().data)); // receive all channels names
                            System.out.println(counter + " - " + tempListChannels.get(counter));
                        }

                        System.out.println("What channel to you want to see?");
                        int chosenChannel = Integer.parseInt(stdin.readLine());

                        taggedConnection.send(0,tempListChannels.get(chosenChannel).getBytes()); // send specific channel name

                        int sizeListPosts = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list of posts

                        String message;

                        if(sizeListPosts > 0) {
                            for (int counter = 0; counter < sizeListPosts; counter++) {
                                message = new String(taggedConnection.receive().data); // receive post transformed into formatted string and then bytes
                                System.out.println("\n" + message);
                            }
                        } else {
                            System.out.println("\n--WARNING: No posts have been sent in channel: " + tempListChannels.get(chosenChannel) + " --");
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

                        System.out.println("What channel to you want to see?");
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

                    //TODO: client get list of posts from various channels
                }

                case 0 -> System.out.println("\n--WARNING: You chose exiting the program.--");

                default -> System.out.println("\n--WARNING: You chose poorly... Please try again.");
            }
        } while(Integer.parseInt(menu) != 0);

        /* USER HAS CHOSEN TO LOGOUT */

        System.out.println("\nCLOSING SOCKET...");
        taggedConnection.close();
        socket.close();
        System.out.println("SOCKET CLOSED.");
    }
}
