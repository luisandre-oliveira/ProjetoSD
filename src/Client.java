import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Client {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", PORT);
        TaggedConnection taggedConnection = new TaggedConnection(socket);
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in)); // read from keyboard

        String userName = "", userPass; // user information
        boolean userLoginStatus = false; // true if logged
        boolean userAdminStatus = false; // true if admin
        String menu;

        while(!userLoginStatus) {
            // Read the socket for the prompt, then reading the username from keyboard, and finally sending it
            System.out.println("Enter username: "); //Enter username prompt
            userName = stdin.readLine(); // User writes their username

            taggedConnection.send(0,userName.getBytes()); // Send username

            // Read the socket for the prompt, then reading the password from keyboard, and finally sending it
            System.out.println("Enter password: "); //Enter password prompt
            userPass = stdin.readLine(); // User writes their password

            taggedConnection.send(0,userPass.getBytes()); // Send user's password

            // Read the socket for to see if logged or not
            String state = new String(taggedConnection.receive().data); // receive state of login

            if(Objects.equals(state, String.valueOf(HttpsURLConnection.HTTP_OK))) {
                userLoginStatus = true;
                System.out.println("\n--SUCCESS: login successful.--");
            } else if(Objects.equals(state,String.valueOf(HttpsURLConnection.HTTP_FORBIDDEN))){
                System.out.println("\n--WARNING: login unsuccessful.--");
            }
        }

        System.out.println("\n--WARNING: ALWAYS CHOOSE THE NUMBER REFERRING TO THE OPTION YOU WANT!!--");

        /* USER IS NOW LOGGED IN */

        do {
            System.out.println("\nWelcome to main menu!!");
            System.out.println("1 - (ADMIN) Create a channel.");
            System.out.println("2 - (ADMIN) Close a channel.");
            System.out.println("3 - See posts in a channel.");
            System.out.println("4 - Post in a channel.");
            System.out.println("5 - Get a list of posts from various channels.");
            System.out.println("6 - Wait for a post in a channel.");
            System.out.println("0 - Exit the program.");
            System.out.println("\nWhat do you wish to do?");

            menu = stdin.readLine(); // get the menu option from the user
            taggedConnection.send(0,menu.getBytes()); // send the option so the server can decide if it can advance or not

            switch (Integer.parseInt(menu)) {
                case 1 -> {
                    System.out.println("\nYou chose creating a channel.");
                    String state = new String(taggedConnection.receive().data); // receive state of user admin status

                    if(userAdminStatus || Objects.equals(state, String.valueOf(HttpsURLConnection.HTTP_ACCEPTED))) {
                        userAdminStatus = true;

                        System.out.println("What should the name of the channel be?");
                        String name = stdin.readLine(); // get from keyboard name of channel

                        taggedConnection.send(0,name.getBytes()); // send channel name

                        String message = new String(taggedConnection.receive().data); // receive channel create status

                        if(message.equals(String.valueOf(HttpsURLConnection.HTTP_CREATED))) {
                            System.out.println("\n--Channel created successfully.--");
                        } else if(message.equals(String.valueOf(HttpsURLConnection.HTTP_CONFLICT))) {
                            System.out.println("\n--ERROR: Channel name already exists.--");
                        } else {
                            System.out.println("\n--ERROR: Unrecognized error.--");
                        }
                    } else {
                        System.out.println("\n--ERROR: User doesn´t have admin status.--\n");
                    }
                }

                case 2 -> {
                    System.out.println("\nYou chose closing a channel.");
                    String state = new String(taggedConnection.receive().data); // receive admin status

                    if(userAdminStatus || Objects.equals(state, String.valueOf(HttpsURLConnection.HTTP_ACCEPTED))) {
                        userAdminStatus = true;
                    } else if(Objects.equals(state, String.valueOf(HttpsURLConnection.HTTP_UNAUTHORIZED))){
                        System.out.println("\n--ERROR: User doesn´t have admin status.--\n");
                    } else {
                        System.out.println("\nERROR: Unrecognized error.--");
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
                        int chosenChannelId = Integer.parseInt(stdin.readLine());

                        taggedConnection.send(0, tempListOpenChannels.get(chosenChannelId).getBytes()); // send specific channel name

                        String message = new String(taggedConnection.receive().data);
                        if(Objects.equals(message,String.valueOf(HttpsURLConnection.HTTP_OK))) {
                            System.out.println("\n--Channel closed successfully.--");
                        } else if(Objects.equals(message,String.valueOf(HttpsURLConnection.HTTP_NOT_FOUND))) {
                            System.out.println("\n--ERROR: Channel doesn't exist.--");
                        } else {
                            System.out.println("\n--ERROR: Unrecognized error.--");
                        }
                    }
                }

                case 3 -> {
                    System.out.println("\nYou chose seeing posts in a channel.");

                    int sizeListChannels = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list of channels
                    System.out.println("There are " + sizeListChannels + " channels live.");

                    if(sizeListChannels > 0) { // go through every channel
                        ArrayList<String> tempListNameChannels = new ArrayList<>();

                        for (int counter = 0; counter < sizeListChannels; counter++) {
                            tempListNameChannels.add(new String(taggedConnection.receive().data)); // receive all channels names
                            System.out.println(counter + " - " + tempListNameChannels.get(counter)); // print all the channels names
                        }

                        System.out.println("What channel to you want to see?");
                        int chosenChannelIdId = Integer.parseInt(stdin.readLine()); // read from keyboard the id of the channel

                        taggedConnection.send(0,tempListNameChannels.get(chosenChannelIdId).getBytes()); // send specific channel name in list

                        int sizeListPostsInChannel = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list of posts

                        String message;

                        if(sizeListPostsInChannel > 0) {
                            for (int counter = 0; counter < sizeListPostsInChannel; counter++) {
                                message = new String(taggedConnection.receive().data); // receive post transformed into formatted string and then bytes
                                System.out.println(message);
                            }
                        } else {
                            System.out.println("\n--WARNING: No posts have been sent in channel: " + tempListNameChannels.get(chosenChannelIdId) + ". --");
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
                        int chosenChannelIdId = Integer.parseInt(stdin.readLine());

                        taggedConnection.send(0,tempListChannels.get(chosenChannelIdId).getBytes()); // send specific channel name

                        System.out.println("What is the message for the post?");
                        String messageText = stdin.readLine();
                        taggedConnection.send(0,userName.getBytes());
                        taggedConnection.send(1,messageText.getBytes());

                        String message = new String(taggedConnection.receive().data); // receive post status
                        if(Objects.equals(message,String.valueOf(HttpsURLConnection.HTTP_CREATED))) {
                            System.out.println("\n--Post sent successfully.--");
                        } else {
                            System.out.println("\n--ERROR: Unrecognized error.--");
                        }
                    } else {
                        System.out.println("\n--WARNING: There are no open channels.--");
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
                        System.out.println("\n--WARNING: There are no channels live.--");
                    }
                }

                case 6 -> {
                    System.out.println("\nYou chose to wait for a post to be sent to a channel.");

                    int sizeListOpenChannels = Integer.parseInt(new String(taggedConnection.receive().data)); // receive size of list of channels
                    System.out.println("There are " + sizeListOpenChannels + " channels live.");

                    if(sizeListOpenChannels > 0) { // go through every channel
                        ArrayList<String> tempListNameOpenChannels = new ArrayList<>();

                        for (int counter = 0; counter < sizeListOpenChannels; counter++) {
                            tempListNameOpenChannels.add(new String(taggedConnection.receive().data)); // receive all channels names
                            System.out.println(counter + " - " + tempListNameOpenChannels.get(counter)); // print all the channels names
                        }

                        System.out.println("What channel to you want to see?");
                        int chosenChannelName = Integer.parseInt(stdin.readLine()); // read from keyboard the id of the channel

                        taggedConnection.send(0,tempListNameOpenChannels.get(chosenChannelName).getBytes()); // send specific channel name

                        int tempNumberPostsBefore = Integer.parseInt(new String(taggedConnection.receive().data)); // receive number posts before
                        int tempNumberPostsAfter = tempNumberPostsBefore;

                        System.out.println("\n--Waiting for post to arrive.--\n");

                        while (tempNumberPostsBefore == tempNumberPostsAfter) {
                            TimeUnit.MILLISECONDS.sleep(200);
                            tempNumberPostsAfter = Integer.parseInt(new String(taggedConnection.receive().data)); // receive number posts after
                            taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_NOT_FOUND).getBytes()); // send state
                        }

                        taggedConnection.send(0,String.valueOf(HttpsURLConnection.HTTP_CREATED).getBytes());
                        /* due to the 200ms sleep, an extra message is sent, that is trash */
                        taggedConnection.receive();

                        String newPost = new String(taggedConnection.receive().data); // receive post transformed into formatted string and then bytes
                        System.out.println("NEW POST -> " + newPost);


                    } else {
                        System.out.println("\n--WARNING: There are no open channels.--");
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
