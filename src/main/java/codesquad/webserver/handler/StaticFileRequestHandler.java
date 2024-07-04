package codesquad.webserver.handler;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.ContentType;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import codesquad.util.StringUtil;
import codesquad.webserver.StaticFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StaticFileRequestHandler implements RouterHandler {
    private static final Logger logger = LoggerFactory.getLogger(StaticFileRequestHandler.class);
    private final StaticFileReader staticFileReader = new StaticFileReader();

    @Override
    public boolean support(HttpRequest httpRequest) {
        return isStaticRequest(httpRequest.path());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.path();

        String mimeType = "";
        String fileData = "";
        String resourcePath = "";
        Map<String, String> headers = new HashMap<>();

        if (isStaticRequest(path)) {
            mimeType = getContentType(path);
            headers.put("Content-Type", mimeType);
            resourcePath = path;
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

    private String getContentType(String staticFilePath) {
        String extension = StringUtil.getExtension(staticFilePath);
        return ContentType.from(extension).getMimeType();
    }

    private boolean isStaticRequest(String resourcePath) {
        String extension = StringUtil.getExtension(resourcePath);
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return true;
    }

    private String getStaticFile(String path) {
        return staticFileReader.read(path);
    }
}
