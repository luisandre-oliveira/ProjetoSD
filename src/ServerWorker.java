import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerWorker implements Runnable {
    private final Socket clientSocket;

    ServerWorker(Socket cls) {
        this.clientSocket = cls;
    }

    @Override
    public void run() {
        String line;
        String userName, userPass;

        Login login = new Login();

        User luis = new User("luis", "1234", true);
        User jorge = new User("jorge", "1234", true);

        login.addUser(luis);
        login.addUser(jorge);

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
            throw new RuntimeException(e);
        }
    }
}
