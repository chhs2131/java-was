package codesquad.webserver;

import codesquad.http.HttpRequest;
import codesquad.http.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Socket에서 HTTP 요청을 입력받고 파싱합니다.
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public void handle(Socket clientSocket) {
        // 사용자 입력을 받는 부분
        String message = getInputStream(clientSocket);

        // HTTP 파싱
        HttpRequest request = parseHttpMessage(message);
        logger.debug("HTTP Request! {} {} {}", request.method(), request.path(), request.protocol());
        logger.debug("HTTP Headers! size: {}", request.headers().size());
        logger.debug("HTTP Body! {}", request.body());
    }

    private String getInputStream(Socket clientSocket) {
        try {
            InputStream input = clientSocket.getInputStream();
            byte[] inputData = new byte[1024];  // TODO ?? 이건 몇으로 하는게 좋죠?
            int length = input.read(inputData);
            return new String(inputData, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("올바르지 않은 입력입니다.");
        }
    }

    private HttpRequest parseHttpMessage(String message) {
        return HttpRequestParser.parse(message);
    }
}
