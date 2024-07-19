package codesquad.webserver.handler;

import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicRequestHandler implements RouterHandler {
    private static final Logger logger = LoggerFactory.getLogger(DynamicRequestHandler.class);
    private final Map<HandlerPath, ExecutableHandler> requestMap;

    public DynamicRequestHandler(Map<HandlerPath, ExecutableHandler> requestMap) {
        this.requestMap = requestMap;
    }

    @Override
    public boolean support(HttpRequest httpRequest) {
        HandlerPath handlerPath = new HandlerPath(httpRequest.method(), httpRequest.path());
        return requestMap.containsKey(handlerPath);
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        logger.debug("요청을 핸들링합니다. {} {}", httpRequest.method(), httpRequest.path());
        HandlerPath handlerPath = new HandlerPath(httpRequest.method(), httpRequest.path());

        ExecutableHandler handler = requestMap.get(handlerPath);
        if (handler != null) {
            logger.debug("{}의 {}을 실행합니다.", handler.method().getDeclaringClass().getName(), handler.method().getName());
            return handler.invoke(httpRequest);
        }

        return HttpResponse.internalServerError("서버에서 에러가 발생했습니다.");
    }
}
