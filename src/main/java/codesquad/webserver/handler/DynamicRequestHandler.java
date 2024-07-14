package codesquad.webserver.handler;

import codesquad.application.HtmlPageHandler;
import codesquad.application.LoginHandler;
import codesquad.application.UserHandler;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DynamicRequestHandler implements RouterHandler {
    private static final Logger logger = LoggerFactory.getLogger(DynamicRequestHandler.class);
    private static final Map<HandlerPath, Function<HttpRequest, HttpResponse>> mapping = new HashMap<>();
    static {
        // 외부에서 전달받도록 수정 필요
        mapping.put(new HandlerPath(HttpMethod.POST, "/user/login"), LoginHandler::login);
        mapping.put(new HandlerPath(HttpMethod.POST, "/user/logout"), LoginHandler::logout);
        mapping.put(new HandlerPath(HttpMethod.POST, "/user/create"), UserHandler::createUser);
        mapping.put(new HandlerPath(HttpMethod.GET, "/user/list"), UserHandler::getUserList);
        mapping.put(new HandlerPath(HttpMethod.GET, "/index.html"), UserHandler::getHomepage);
        mapping.put(new HandlerPath(HttpMethod.GET, "/"), UserHandler::getHomepage);
        mapping.put(new HandlerPath(HttpMethod.GET, "/registration"), HtmlPageHandler::getRegistrationPage);
        mapping.put(new HandlerPath(HttpMethod.GET, "/login"), HtmlPageHandler::getLoginPage);
    }

    @Override
    public boolean support(HttpRequest httpRequest) {
        HandlerPath handlerPath = new HandlerPath(httpRequest.method(), httpRequest.path());
        return mapping.containsKey(handlerPath);
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        logger.debug("요청을 핸들링합니다. {} {}", httpRequest.method(), httpRequest.path());
        HandlerPath handlerPath = new HandlerPath(httpRequest.method(), httpRequest.path());

        Function<HttpRequest, HttpResponse> handler = mapping.get(handlerPath);
        if (handler != null) {
            return handler.apply(httpRequest);
        }

        return HttpResponse.internalServerError("서버에서 에러가 발생했습니다.");
    }
}
