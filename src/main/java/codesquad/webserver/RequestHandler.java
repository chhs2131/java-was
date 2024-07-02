package codesquad.webserver;

import codesquad.http.HttpRequest;
import codesquad.http.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Socket에서 HTTP 요청을 입력받고 파싱합니다.
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public HttpRequest handle(String message) {
        // HTTP 파싱
        HttpRequest request = parseHttpMessage(message);
        logger.debug("HTTP Request! {} {} {}", request.method(), request.path(), request.protocol());
        logger.debug("HTTP Headers! size: {}", request.headers().size());
        logger.debug("HTTP Body! {}", request.body());

        return request;
    }

    private HttpRequest parseHttpMessage(String message) {
        return HttpRequestParser.parse(message);
    }
}
