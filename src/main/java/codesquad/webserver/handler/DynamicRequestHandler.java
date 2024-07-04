package codesquad.webserver.handler;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import codesquad.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicRequestHandler implements RouterHandler {
    private static final Logger logger = LoggerFactory.getLogger(DynamicRequestHandler.class);

    @Override
    public boolean support(HttpRequest httpRequest) {
        // 일단 create 하나로 테스트
        return httpRequest.path().equals("/create");
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.path();

        if (path.equals("/create")) {
            String name = httpRequest.queryString().get("name");
            String password = httpRequest.queryString().get("password");
            String nickname = httpRequest.queryString().get("nickname");
            String email = httpRequest.queryString().get("email");

            final User user = new User(name, password, nickname, email);
            logger.debug("회원가입을 완료했습니다. {}", user);

            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.CREATED, null, "유저가 생성되었습니다.");
        }

        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.INTERNAL_SERVER_ERROR, null, "서버에서 에러가 발생했습니다.");
    }
}
