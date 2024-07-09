package codesquad.application;

import codesquad.database.UserDatabase;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import codesquad.model.User;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private static SessionManager sessionManager = new SessionManager();

    // POST /user/create
    public static HttpResponse createUser(HttpRequest httpRequest) {
        String name = httpRequest.body().get("name");
        String password = httpRequest.body().get("password");
        String nickname = httpRequest.body().get("nickname");
        String email = httpRequest.body().get("email");

        final User user = new User(name, password, nickname, email);
        UserDatabase.addUser(user);
        logger.debug("회원가입을 완료했습니다. {}", user);

        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, Map.of("Location", "/index.html"), "유저가 생성되었습니다.");
    }

    // POST /user/login
    public static HttpResponse login(HttpRequest httpRequest) {
        String name = httpRequest.body().get("username");
        String password = httpRequest.body().get("password");

        try {
            User user = UserDatabase.getUserByIdAndPassword(name, password);
            Session session = sessionManager.createSession(user);
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, Map.of("Location", "/index.html", "Set-Cookie", "SID="+session.id()+";Path=/;"), "굿");
        } catch (IllegalArgumentException e) {
            logger.debug("유저 정보가 올바르지 않습니다. 이름:{}", name);
            // TODO BadRequest가 옳은 응답인것 같은데
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, Map.of("Location", "/user/login_failed.html"), null);
        }
    }

    // POST /user/logout
    public static HttpResponse logout(HttpRequest httpRequest) {
        httpRequest.headers().get("Cookie"); // TODO 작성필요!
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, Map.of("Location", "/index.html"), "굿");
    }
}
