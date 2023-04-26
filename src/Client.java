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

        String userName, userPass;
        boolean logged = false;
        String state;
        String line;
        String menu;

        while(!logged) {
            // Read the socket for the prompt, then reading the username from keyboard, and finally sending it
            System.out.println(in.readLine()); //Enter username prompt from server
            userName = stdin.readLine(); // User writes their username
            out.println(userName);
            out.flush();

            // Read the socket for the prompt, then reading the password from keyboard, and finally sending it
            System.out.println(in.readLine()); //Enter password prompt from server
            userPass = stdin.readLine(); // User writes their password
            out.println(userPass);
            out.flush();

            // Read the socket for to see if logged or not
            state = in.readLine();

            if(Objects.equals(state, Boolean.TRUE.toString())) {
                logged = true;
                System.out.println("\n--login successful--");
            } else {
                System.out.println("\n--login unsuccessful--");
            }
        }

        /* USER IS NOW LOGGED IN */

        do {
            //TODO: this is really dumb, need to improve this, doesn't work if I use 'while ((line = in.readLine()) != null)'
            for(int x= 0; x <10; x++) {
                line = in.readLine();
                System.out.println(line);
            }

            menu = stdin.readLine(); // get the menu option from the user

            out.println(menu);
            out.flush();

            String result = in.readLine();
            System.out.println(result);
        } while(Integer.parseInt(menu) != 0);

        /* USER HAS CHOSEN TO LOGOUT */

        System.out.println("CLOSING SOCKET...");
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
        System.out.println("SOCKET CLOSED.");
    }
}
