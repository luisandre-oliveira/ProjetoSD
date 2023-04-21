import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;
    private final int PORT = 8080;

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread thread = new Thread(new ServerWorker(clientSocket));
            thread.start();
        }
    }

    public static void main(String[] args) throws IOException {
        Server s = new Server();
    }
}
