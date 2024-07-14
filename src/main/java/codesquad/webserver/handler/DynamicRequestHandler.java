package codesquad.webserver.handler;

import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicRequestHandler implements RouterHandler {
    private static final Logger logger = LoggerFactory.getLogger(DynamicRequestHandler.class);
    private final Map<HandlerPath, Method> requestMap;
    private final Map<Class<?>, Object> instances;

    public DynamicRequestHandler(final Map<HandlerPath, Method> requestMap, final Map<Class<?>, Object> map) {
        this.requestMap = requestMap;
        this.instances = map;
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

        Method handler = requestMap.get(handlerPath);
        if (handler != null) {
            try {
                return (HttpResponse) handler.invoke(instances.get(handler.getDeclaringClass()), httpRequest);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.debug("리플렉션 핸들링 에러");
                return HttpResponse.internalServerError("서버에서 에러가 발생했습니다.");
            }
        }

        return HttpResponse.internalServerError("서버에서 에러가 발생했습니다.");
    }
}
