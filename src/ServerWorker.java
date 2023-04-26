import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class ServerWorker implements Runnable {
    private final Socket clientSocket;

    ServerWorker(Socket cls) {
        this.clientSocket = cls;
    }

    @Override
    public void run() {
        String line;
        String userName = "", userPass;
        int menu;

        Login login = new Login();

        // initialization of two super-users with admin capabilities
        User luis = new User("luis", "1234", true);
        User jorge = new User("jorge", "1234", true);
        User comum = new User("comum", "1234", false);
        login.addUser(luis);
        login.addUser(jorge);
        login.addUser(comum);

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream()); // write to socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // read from socket

            boolean logged = false;

            while(!logged) {
                out.println("Enter username: ");
                out.flush();
                userName = in.readLine();

                out.println("Enter password: ");
                out.flush();
                userPass = in.readLine();

                if(login.checkCredentials(userName,userPass)) {
                    System.out.println("User " + userName + " logged in");
                    logged = true;
                }

                out.println(logged); //state
                out.flush();
            }

            /* USER IS NOW LOGGED IN */

            do {
                out.println("\nWelcome to main menu!!");
                out.println("1 - (ADMIN) Create a channel.");
                out.println("2 - (ADMIN) Close a channel.");
                out.println("3 - See posts in a channel.");
                out.println("4 - Post in a channel.");
                out.println("5 - Get a list of posts from various channels.");
                out.println("0 - Exit the program."); // TODO: maybe later add the functionality do just logout instead of exit
                out.println("\nWhat do you wish to do?");
                out.flush();

                menu = Integer.parseInt(in.readLine()); // get the menu option

                switch (menu) {
                    case 0 -> {
                        out.println("You chose exiting the program.");
                    }
                    case 1 -> {
                        out.println("You chose creating a channel.");

                        for(User u: login.getLista()) {
                            if(Objects.equals(u.getUsername(), userName)) {
                                if(u.isUserAdmin()) {
                                    System.out.println("YES");
                                } else {
                                    System.out.println("NO");
                                }
                            }
                        }
                    }
                    case 2 -> out.println("You chose closing a channel.");
                    case 3 -> {
                        out.println("You chose seeing posts in a channel.");


                    }
                    case 4 -> out.println("You chose posting a channel.");
                    default -> out.println("You chose poorly... Please try again.");
                }
                out.flush();
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
