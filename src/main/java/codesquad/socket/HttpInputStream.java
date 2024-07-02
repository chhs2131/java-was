package codesquad.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class HttpInputStream {
    private final Socket socket;

    public HttpInputStream(Socket socket) {
        this.socket = socket;
    }

    public String read() {
        return getInputStream(socket);
    }

    private String getInputStream(Socket clientSocket) {
        try {
            InputStream input = clientSocket.getInputStream();
            byte[] inputData = new byte[1024];

            int length = input.read(inputData);
            return new String(inputData, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("올바르지 않은 입력입니다.");
        }
    }
}
