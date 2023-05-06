import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection {
    public static class Frame {
        public final int tag;
        public final byte[] data;

        public Frame(int tag, byte[] data) {
            this.tag = tag;
            this.data = data;
        }
    }

    private final DataOutputStream outputStream;
    private final DataInputStream inputStream;

    private final ReentrantLock writeLock;
    private final ReentrantLock readLock;

    public TaggedConnection(Socket socket) throws IOException {
        this.outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.writeLock = new ReentrantLock();
        this.readLock = new ReentrantLock();
    }

    public void send(Frame frame) throws IOException {
        this.writeLock.lock();
        try {
            outputStream.writeInt(frame.tag);
            outputStream.writeInt(frame.data.length);
            outputStream.write(frame.data);
            outputStream.flush();
        } finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag, byte[] data) throws IOException {
        this.writeLock.lock();
        try {
            outputStream.writeInt(tag);
            outputStream.writeInt(data.length);
            outputStream.write(data);
            outputStream.flush();
        } finally {
            this.writeLock.unlock();
        }
    }

    public Frame receive() throws IOException {
        this.readLock.lock();
        try {
            int tag = inputStream.readInt();
            int size = inputStream.readInt();
            byte[] bytes_read = new byte[size];
            inputStream.readFully(bytes_read);
            return new Frame(tag,bytes_read);
        } finally {
            this.readLock.unlock();
        }
    }

    public void close() throws IOException {
        this.outputStream.close();
        this.inputStream.close();
    }
}
