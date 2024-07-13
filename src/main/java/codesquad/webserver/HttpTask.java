package codesquad.webserver;

import codesquad.webserver.filter.SessionFilter;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.parser.HttpRequestParser;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.ResponseConverter;
import codesquad.webserver.socket.SocketReader;
import codesquad.webserver.socket.SocketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * 전달 받은 Socket을 기반으로 HTTP 요청 해석 및 적절한 응답을 진행합니다.
 * @param clientSocket 요청 확인 및 응답을 진행할 클라이언트 소켓
 * @param requestHandler HTTP Request를 해석하고 Response를 반환하는 객체
 */
public record HttpTask(Socket clientSocket, RequestHandler requestHandler) implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HttpTask.class);

    @Override
    public void run() {
        try {
            SocketReader socketReader = new SocketReader(clientSocket);
            SocketWriter socketWriter = new SocketWriter(clientSocket);
            String message = socketReader.read();

            HttpRequest request = HttpRequestParser.parse(message);
            logger.debug("HTTP Request: {} {} {}", request.method(), request.path(), request.protocol());
            logger.debug("HTTP Headers: size: {}", request.headers().size());
            logger.debug("HTTP Body: {}", request.body());

            // TODO Filter 묶음을 만들고 그곳에 사용할 필터들을 등록한뒤 처리할 것
            final SessionFilter sessionFilter = new SessionFilter();
            request = sessionFilter.preFilter(request);

            HttpResponse response = requestHandler.handle(request);
            byte[] socketBytes = ResponseConverter.toSocketBytes(response);
            socketWriter.write(socketBytes);

            clientSocket.close();
        } catch (IOException e) {
            logger.error("요청 처리를 실패했습니다. {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
