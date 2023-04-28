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
        int menu;
        String line;
        String userName = "", userPass;
        Boolean flag = false;

        Login login = new Login();

        // initialization of two super-users who are admin and one normal user with no admin privileges
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
                userName = in.readLine();
                userPass = in.readLine();

                if(login.checkCredentials(userName,userPass)) {
                    System.out.println("User " + userName + " logged in");
                    logged = true;
                }

                out.println(logged); //state of log (true/false)
                out.flush();
            }

            /* USER IS NOW LOGGED IN */

            do {
                menu = Integer.parseInt(in.readLine()); // get the menu option from the client

                if(menu == 1 || menu == 2) {
                    flag = login.isUserAdmin(userName);
                    out.println(flag);
                    out.flush();
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
