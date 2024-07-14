package codesquad.webserver.handler;

import codesquad.webserver.file.FileHttpResponseCreator;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticRequestHandler implements RouterHandler {
    protected static final Logger logger = LoggerFactory.getLogger(StaticRequestHandler.class);

    @Override
    public boolean support(HttpRequest httpRequest) {
        String path = httpRequest.path();
        return isStaticRequest(path);
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String resourcePath = httpRequest.path();
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

    private boolean isStaticRequest(String resourcePath) {
        String extension = StringUtil.getExtension(resourcePath);
        return extension != null && !extension.isEmpty();
    }
}
