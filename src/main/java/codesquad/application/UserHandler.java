package codesquad.application;

import codesquad.database.UserDatabase;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.Cookie;
import codesquad.http.type.HttpHeader;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import codesquad.model.User;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        HttpHeader headers = HttpHeader.of("Location", "/index.html");
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, "유저가 생성되었습니다.");
    }

    // POST /user/login
    public static HttpResponse login(HttpRequest httpRequest) {
        String name = httpRequest.body().get("username");
        String password = httpRequest.body().get("password");

        try {
            User user = UserDatabase.getUserByIdAndPassword(name, password);
            Session session = sessionManager.createSession(user);

            // TODO Cookie 객체를 활용하도록 수정 필요! (Session 정보를 기반으로 Cookie를 생성하면될 듯)
            HttpHeader headers = HttpHeader.of(
                "Location", "/main/index.html",
                "Set-Cookie", "SID="+session.id()+";Path=/;Max-Age=3600;Expires=Wed, 21 Oct 2025 07:28:00 KST");
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, "굿");
        } catch (IllegalArgumentException e) {
            logger.debug("유저 정보가 올바르지 않습니다. 이름:{}", name);

            HttpHeader headers = new HttpHeader();
            headers.add("Location", "/user/login_failed.html");
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, null);
        }
    }

    // POST /user/logout
    public static HttpResponse logout(HttpRequest httpRequest) {
        final Cookie cookies = httpRequest.headers().getCookies();
        final String sid = cookies.get("SID");

        sessionManager.removeSession(sid);
        logger.debug("정상적으로 로그아웃하였습니다. sid:{}", sid);

        HttpHeader headers = HttpHeader.of(
            "Location", "/index.html",
            "Set-Cookie", "SID=;Path=/;Max-Age=0");
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, "로그아웃하였습니다.");
    }
}
