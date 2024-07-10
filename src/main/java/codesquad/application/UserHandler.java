package codesquad.application;

import codesquad.database.UserDatabase;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.Cookie;
import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpProtocol;
import codesquad.webserver.http.type.HttpStatus;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private static SessionManager sessionManager = new SessionManager();

    private UserHandler() {}

    // POST /user/create
    public static HttpResponse createUser(HttpRequest httpRequest) {
        String name = httpRequest.body().get("name");
        String password = httpRequest.body().get("password");
        String nickname = httpRequest.body().get("nickname");
        String email = httpRequest.body().get("email");

        final User user = new User(name, password, nickname, email);
        UserDatabase.addUser(user);
        logger.debug("회원가입을 완료했습니다. {}", user);

        HttpHeader headers = HttpHeader.createRedirection("/index.html");
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, "유저가 생성되었습니다.");
    }

    // POST /user/login
    public static HttpResponse login(HttpRequest httpRequest) {
        String name = httpRequest.body().get("username");
        String password = httpRequest.body().get("password");

        try {
            User user = UserDatabase.getUserByIdAndPassword(name, password);
            Session session = sessionManager.createSession(user);

            HttpHeader headers = HttpHeader.createRedirection("/main/index.html");
            headers.setCookie(Cookie.from(session));
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, "로그인 완료!");
        } catch (IllegalArgumentException e) {
            logger.debug("유저 정보가 올바르지 않습니다. 이름:{}", name);

            HttpHeader headers = HttpHeader.createRedirection("/user/login_failed.html");
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, null);
        }
    }

    // POST /user/logout
    public static HttpResponse logout(HttpRequest httpRequest) {
        final Cookie cookies = httpRequest.headers().getCookies();
        final String sid = cookies.get("SID");

        if (!sessionManager.validSession(sid)) {
            logger.debug("세션이 존재하지 않습니다. sid:{}", sid);
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.UNAUTHORIZED, HttpHeader.createEmpty(), "세션이 존재하지 않습니다.");
        }
        sessionManager.removeSession(sid);
        logger.debug("정상적으로 로그아웃하였습니다. sid:{}", sid);

        HttpHeader headers = HttpHeader.createRedirection("/index.html");
        headers.setCookie(Cookie.expiredSession());
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, "로그아웃하였습니다.");
    }
}
