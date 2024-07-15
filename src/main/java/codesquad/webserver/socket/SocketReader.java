package codesquad.webserver.socket;

import codesquad.webserver.exception.SocketIoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketReader {
    private static final int BUFFER_SIZE = 1024;
    private final Socket socket;

    public SocketReader(Socket socket) {
        this.socket = socket;
    }

    /**
     * 전달된 Socket의 InputStream을 통해 값을 읽습니다.
     * @return String 값
     * @throws SocketIoException 소켓 IOException 발생시
     */
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
            throw new SocketIoException("Socket 연결이 불안정합니다.");
        }
    }
}
