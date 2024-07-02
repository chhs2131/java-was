package codesquad.webserver;

import codesquad.http.HttpRequest;

import java.io.IOException;
import java.net.Socket;

/**
 * 전달 받은 Socket을 기반으로 HTTP 요청 해석 및 적절한 응답을 진행합니다.
 * @param clientSocket 요청 확인 및 응답을 진행할 클라이언트 소켓
 * @param requestHandler HTTP Request를 해석하고 핸들링하는 객체
 * @param responseHandler 적절한 HTTP Response를 생성하고 반환하는 객체
 */
public record HttpTask(Socket clientSocket, RequestHandler requestHandler, ResponseHandler responseHandler) implements Runnable {
    @Override
    public void run() {
        try {
            HttpRequest request = requestHandler.handle(clientSocket);
            responseHandler.handle(clientSocket, request);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("잘못된 HTTP 요청입니다.");
        }
    }
}
