import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public Server() throws IOException {
        final int PORT = 8080;
        final ServerSocket serverSocket = new ServerSocket(PORT);

        Records records = new Records();
        records.addToListChannels(new Channel("Testing Channel",true));

        User luis = new User("luis", "1234", true);
        User jorge = new User("jorge", "1234", true);
        User comum = new User("comum", "1234", false);

        Login login = new Login();
        login.addUser(luis);
        login.addUser(jorge);
        login.addUser(comum);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread thread = new Thread(new ServerWorker(clientSocket, records, login));
            thread.start();

            System.out.println("\nNumber of threads running: " + getNumberThreadsCurrentlyRunning());
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
    }

    public int getNumberThreadsCurrentlyRunning() {
        int nThreadsRunning = 0;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getState() == Thread.State.RUNNABLE)
                nThreadsRunning++;
        }
        return nThreadsRunning;
    }
}
