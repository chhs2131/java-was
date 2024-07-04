package codesquad.webserver;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.ResponseConverter;
import codesquad.socket.SocketReader;
import codesquad.socket.SocketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * 전달 받은 Socket을 기반으로 HTTP 요청 해석 및 적절한 응답을 진행합니다.
 * @param clientSocket 요청 확인 및 응답을 진행할 클라이언트 소켓
 * @param requestHandler HTTP Request를 해석하고 핸들링하는 객체
 * @param responseHandler 적절한 HTTP Response를 생성하고 반환하는 객체
 */
public record HttpTask(Socket clientSocket, RequestHandler requestHandler, ResponseHandler responseHandler) implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HttpTask.class);

    @Override
    public void run() {
        try {
            SocketReader socketReader = new SocketReader(clientSocket);
            SocketWriter socketWriter = new SocketWriter(clientSocket);
            String message = socketReader.read();

            HttpRequest request = requestHandler.handle(message);
            HttpResponse response = responseHandler.handle(request);

            byte[] socketBytes = ResponseConverter.toSocketBytes(response);
            socketWriter.write(socketBytes);

            clientSocket.close();
        } catch (IOException e) {
            logger.error("요청 처리를 실패했습니다. {}", e.getMessage());
            e.printStackTrace();

        }
    }
}
