package codesquad.webserver.socket;

import codesquad.webserver.exception.SocketIoException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketWriter {
    private final Socket socket;

    public SocketWriter(Socket socket) {
        this.socket = socket;
    }

    /**
     * 전달된 Socket의 OutputStream을 통해 값을 전달합니다.
     * @param message
     * @throws SocketIoException 소켓 IOException 발생시
     */
    public void write(byte[] message) {
        try {
            OutputStream clientOutput = socket.getOutputStream();
            clientOutput.write(message);
            clientOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SocketIoException("Socket 연결이 불안정합니다.");
        }
    }
}
