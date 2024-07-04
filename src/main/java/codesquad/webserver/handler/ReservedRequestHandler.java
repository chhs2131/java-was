package codesquad.webserver.handler;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.ContentType;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import codesquad.webserver.StaticFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ReservedRequestHandler implements RouterHandler {
    private static final Logger logger = LoggerFactory.getLogger(ReservedRequestHandler.class);
    private final StaticFileReader staticFileReader = new StaticFileReader();

    @Override
    public boolean support(HttpRequest httpRequest) {
        return httpRequest.path().equals("/registration");
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.path();

        String mimeType = "";
        String fileData = "";
        String resourcePath = "";
        Map<String, String> headers = new HashMap<>();

        if (path.equals("/registration")) {
            mimeType = ContentType.HTML.getMimeType();
            headers.put("Content-Type", mimeType);
            resourcePath = "/registration/index.html";
        }

        // 정적 파일이 아닌 경우
        if (resourcePath.isEmpty()) {
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.BAD_REQUEST, null, "요청이 잘못된 것 같은데요?");
        }

        // 정적 파일 응답
        try {
            fileData = getStaticFile(resourcePath);
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.OK, headers, fileData);
        } catch (Exception e) {
            logger.debug("HTTP NotFound Exception. {}", resourcePath);
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.NOT_FOUND, null, "못찾겠습니다~");
        }
    }

    private String getStaticFile(String path) {
        return staticFileReader.read(path);
    }
}
