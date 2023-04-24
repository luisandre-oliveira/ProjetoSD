import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    private final int PORT = 8080;

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);

        while (true) {
            System.out.println("Number of threads running: " + getNumberThreadsCurrentlyRunning());

            Socket clientSocket = serverSocket.accept();
            Thread thread = new Thread(new ServerWorker(clientSocket));
            thread.start();

            System.out.println("Number of threads running: " + getNumberThreadsCurrentlyRunning());
        }
    }

    public static void main(String[] args) throws IOException {
        Server s = new Server();
    }

    public int getNumberThreadsCurrentlyRunning() {
        int nbRunning = 0;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getState()==Thread.State.RUNNABLE)
                nbRunning++;
        }
        return nbRunning;
    }
}
