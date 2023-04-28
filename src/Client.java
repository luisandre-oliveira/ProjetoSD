import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class Client {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", PORT);

        PrintWriter out = new PrintWriter(socket.getOutputStream()); // write to socket
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in)); // keyboard
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // read from socket

        String userName, userPass; // user information
        boolean userLoginStatus = false; // true if logged
        boolean userAdminStatus = false; // true if not admin
        String line;
        String state, menu;

        while(!userLoginStatus) {
            // Read the socket for the prompt, then reading the username from keyboard, and finally sending it
            System.out.println("Enter username: "); //Enter username prompt
            userName = stdin.readLine(); // User writes their username
            out.println(userName);
            out.flush();

            // Read the socket for the prompt, then reading the password from keyboard, and finally sending it
            System.out.println("Enter password: "); //Enter password prompt
            userPass = stdin.readLine(); // User writes their password
            out.println(userPass);
            out.flush();

            // Read the socket for to see if logged or not
            state = in.readLine();

            if(Objects.equals(state, Boolean.TRUE.toString())) {
                userLoginStatus = true;
                System.out.println("\n--login successful--");
            } else {
                System.out.println("\n--login unsuccessful--");
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
            System.out.println("0 - Exit the program."); // TODO: maybe later add the functionality do just logout instead of exit
            System.out.println("\nWhat do you wish to do?");

            menu = stdin.readLine(); // get the menu option from the user
            out.println(menu); // send the option so the server can decide if it can advance or not
            out.flush();

            switch (Integer.parseInt(menu)) {
                case 1 -> {
                    System.out.println("You chose creating a channel.");
                    state = in.readLine();

                    if(userAdminStatus || Objects.equals(state, Boolean.TRUE.toString())) {
                        userAdminStatus = true;
                    } else {
                        System.out.println("\n--user doesn´t have admin status--");
                    }

                    //TODO: código de criação de canal
                }

                case 2 -> {
                    System.out.println("You chose closing a channel.");
                    state = in.readLine();

                    if(userAdminStatus || Objects.equals(state, Boolean.TRUE.toString())) {
                        userAdminStatus = true;
                    } else {
                        System.out.println("\n--user doesn´t have admin status--");
                    }

                    //TODO: código para apagar canal
                }

                case 3 -> {
                    System.out.println("You chose seeing posts in a channel.");
                    //TODO: código para ver posts num canal
                }

                case 4 -> {
                    System.out.println("You chose posting a channel.");
                    //TODO: código para postar num canal
                }

                case 0 -> System.out.println("You chose exiting the program.");

                default -> System.out.println("You chose poorly... Please try again.");
            }
        } while(Integer.parseInt(menu) != 0);

        /* USER HAS CHOSEN TO LOGOUT */

        System.out.println("CLOSING SOCKET...");
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
        System.out.println("SOCKET CLOSED.");
    }
}
