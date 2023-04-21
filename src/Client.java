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

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        String userName, userPass;
        boolean logged = false;
        String state;

        while(!logged) {
            // Read the socket for the prompt, then reading the username from keyboard, and finally sending it
            System.out.println(in.readLine()); //Enter username prompt
            userName = stdin.readLine();
            out.println(userName);
            out.flush();

            // Read the socket for the prompt, then reading the password from keyboard, and finally sending it
            System.out.println(in.readLine()); //Enter password prompt
            userPass = stdin.readLine();
            out.println(userPass);
            out.flush();

            // Read the socket for to see if logged or not
            state = in.readLine();
            System.out.println(state);

            if(Objects.equals(state, "YES")) {
                    logged = true;
            }

            System.out.println("State: " + logged);
        }

        System.out.println("CLOSING SOCKET...");
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }
}
