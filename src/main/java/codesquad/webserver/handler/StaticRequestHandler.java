package codesquad.webserver.handler;

import codesquad.webserver.file.FileHttpResponseCreator;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StaticRequestHandler implements RouterHandler {
    protected static final Logger logger = LoggerFactory.getLogger(StaticRequestHandler.class);
    private static final Map<String, String> mapping = new HashMap<>();
    static {
        // 외부에서 전달받도록 수정 필요
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
            return HttpResponse.badRequest("잘못된 요청입니다. Path: " + resourcePath);
        }

        try {
            return FileHttpResponseCreator.create(resourcePath);
        } catch (Exception e) {
            logger.debug("HTTP NotFound Exception. {}", resourcePath);
            return HttpResponse.notFound("파일을 찾을 수 없습니다. Path: " + resourcePath);
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
}
