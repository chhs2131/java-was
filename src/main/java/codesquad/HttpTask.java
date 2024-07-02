package codesquad;

import java.io.IOException;
import java.net.Socket;

/**
 * 전달 받은 Socket을 기반으로 HTTP 요청 해석 및 적절한 응답을 진행합니다.
 * @param clientSocket
 * @param requestHandler
 * @param responseHandler
 */
public record HttpTask(Socket clientSocket, RequestHandler requestHandler, ResponseHandler responseHandler) implements Runnable {
    @Override
    public void run() {
        try {
            requestHandler.handle(clientSocket);
            responseHandler.handle(clientSocket);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("잘못된 HTTP 요청입니다.");
        }
    }
}
