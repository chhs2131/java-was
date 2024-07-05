package codesquad.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketReader {
    private static final int BUFFER_SIZE = 1024;
    private final Socket socket;

    public SocketReader(Socket socket) {
        this.socket = socket;
    }

    public String read() {
        return getInputStream(socket);
    }

    private String getInputStream(Socket clientSocket) {
        try {
            InputStream input = clientSocket.getInputStream();
            byte[] buffer = new byte[BUFFER_SIZE];

            StringBuilder sb = new StringBuilder();
            int length = 0;
            do {
                length = input.read(buffer);
                sb.append(new String(buffer, 0, length));
            } while (length == BUFFER_SIZE);

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("올바르지 않은 입력입니다.");
        }
    }
}
