package codesquad.webserver.handler;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.HttpMethod;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import codesquad.model.User;
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
        mapping.put(new HandlerPath(HttpMethod.POST, "/create"), DynamicRequestHandler::getCreateUser);
    }

    @Override
    public boolean support(HttpRequest httpRequest) {
        HandlerPath handlerPath = new HandlerPath(httpRequest.method(), httpRequest.path());
        return mapping.containsKey(handlerPath);
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        HandlerPath handlerPath = new HandlerPath(httpRequest.method(), httpRequest.path());

        Function<HttpRequest, HttpResponse> handler = mapping.get(handlerPath);
        if (handler != null) {
            return handler.apply(httpRequest);
        }

        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.INTERNAL_SERVER_ERROR, null, "서버에서 에러가 발생했습니다.");
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private static HttpResponse getCreateUser(HttpRequest httpRequest) {
        String name = httpRequest.body().get("name");
        String password = httpRequest.body().get("password");
        String nickname = httpRequest.body().get("nickname");
        String email = httpRequest.body().get("email");

        final User user = new User(name, password, nickname, email);
        logger.debug("회원가입을 완료했습니다. {}", user);

        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, Map.of("Location", "/index.html"), "유저가 생성되었습니다.");
    }
}
