package codesquad.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketWriter {
    private final Socket socket;

    public SocketWriter(Socket socket) {
        this.socket = socket;
    }

    public void write(String message) {
        write(message.getBytes());
    }

    public void write(byte[] message) {
        try {
            OutputStream clientOutput = socket.getOutputStream();
            clientOutput.write(message);
            clientOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("응답 반환중 문제가 발생했어요. ^_^");
        }
    }
}
