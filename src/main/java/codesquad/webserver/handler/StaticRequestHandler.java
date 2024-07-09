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

public class StaticRequestHandler implements RouterHandler {
    protected static final Logger logger = LoggerFactory.getLogger(StaticRequestHandler.class);
    protected final StaticFileReader staticFileReader = new StaticFileReader();
    private static final Map<String, String> mapping = new HashMap<>();
    static {
        // 외부에서 전달받도록 수정 필요
        mapping.put("/", "/index.html");
        mapping.put("/registration", "/registration/index.html");
        mapping.put("/login", "/login/index.html");
    }

    @Override
    public boolean support(HttpRequest httpRequest) {
        String path = httpRequest.path();
        if (mapping.containsKey(path)) {
            return true;
        }
        return isStaticRequest(path);
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.path();
        String resourcePath = getResourcePath(path);

        if (resourcePath == null || resourcePath.isEmpty()) {
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.BAD_REQUEST, null, "요청이 잘못된 것 같은데요?");
        }

        String mimeType = getMimeType(resourcePath);

        String fileData = "";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", mimeType);

        try {
            fileData = getStaticFile(resourcePath);
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.OK, headers, fileData);
        } catch (Exception e) {
            logger.debug("HTTP NotFound Exception. {}", resourcePath);
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.NOT_FOUND, null, "파일을 찾을 수 없습니다~");
        }
    }

    protected String getResourcePath(String path) {
        if (mapping.containsKey(path)) {
            return mapping.get(path);
        }
        return path;
    }

    private boolean isStaticRequest(String resourcePath) {
        String extension = StringUtil.getExtension(resourcePath);
        return extension != null && !extension.isEmpty();
    }

    protected String getMimeType(String staticFilePath) {
        String extension = StringUtil.getExtension(staticFilePath);
        return ContentType.from(extension).getMimeType();
    }

    protected String getStaticFile(String path) {
        return staticFileReader.read(path);
    }
}
