package codesquad.application;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import codesquad.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    public static HttpResponse getCreateUser(HttpRequest httpRequest) {
        String name = httpRequest.body().get("name");
        String password = httpRequest.body().get("password");
        String nickname = httpRequest.body().get("nickname");
        String email = httpRequest.body().get("email");

        final User user = new User(name, password, nickname, email);
        logger.debug("회원가입을 완료했습니다. {}", user);

        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, Map.of("Location", "/index.html"), "유저가 생성되었습니다.");
    }
}
