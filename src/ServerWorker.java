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

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            Login login = new Login();

            boolean logged = false;

            out.println("Enter username: ");
            out.flush();
            userName = in.readLine();

            out.println("Enter password: ");
            out.flush();
            userPass = in.readLine();

            /*
            while ((line = in.readLine()) != null) {
                out.println("LOGIN");
                out.flush();
            }
            */

            while(!logged) {
                if(login.checkCredentials(userName,userPass)) {
                    System.out.println("User " + userName + " logged in");
                    logged = true;
                    out.println("YES");
                }

                else {
                    System.out.println("Invalid credentials");
                    out.println("NO");
                }

            }

            clientSocket.shutdownOutput();
            clientSocket.shutdownInput();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
