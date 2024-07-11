package codesquad.application;

import codesquad.database.UserDatabase;
import codesquad.util.StringUtil;
import codesquad.webserver.handler.SimpleTemplateEngine;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.*;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private static final SessionManager sessionManager = new SessionManager();

    private UserHandler() {}

    // GET /
    public static HttpResponse getHomepage(HttpRequest httpRequest) {
        final Cookie cookies = httpRequest.headers().getCookies();
        final String sid = cookies.get("SID");

        String holderValue = "";
        if (sid == null || !sessionManager.validSession(sid)) {
            logger.debug("세션이 존재하지 않습니다. sid:{}", sid);
            holderValue = "<a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>";
        } else {
            User user = (User) sessionManager.getSession(sid).get().attributes().get("user");
            holderValue = user.getName() + "님 환영합니다.";
        }

        // TODO StaticHandler와 코드가 중복됨
        String resourcePath = "/index.html";
        String mimeType = getMimeType(resourcePath);

        HttpHeader headers = new HttpHeader();
        headers.add("Content-Type", mimeType);

        String s = SimpleTemplateEngine.readTemplate(resourcePath);
        String templateHtml = SimpleTemplateEngine.processTemplate(s, Map.of("holder", holderValue));
        logger.debug("동적 페이지를 반환합니다.");

        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.OK, headers, templateHtml);
    }

    protected static String getMimeType(String staticFilePath) {
        String extension = StringUtil.getExtension(staticFilePath);
        return ContentType.from(extension).getMimeType();
    }

    // GET /user/list
    public static HttpResponse getUserList(HttpRequest httpRequest) {
        final Cookie cookies = httpRequest.headers().getCookies();
        final String sid = cookies.get("SID");

        if (sid == null || !sessionManager.validSession(sid)) {
            logger.debug("세션이 존재하지 않습니다. sid:{}", sid);
            HttpHeader headers = HttpHeader.createRedirection("/user/login_failed.html");
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, null);
        }

        // HTML 값 생성
        List<User> users = UserDatabase.findAll();
        StringBuilder sb = new StringBuilder();
        users.forEach(user -> {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(user.getName());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(user.getEmail());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(user.getNickname());
            sb.append("</td>");
            sb.append("</tr>");
        });

        // HTML 페이지 불러오기
        String resourcePath = "/user/list.html";

        String mimeType = getMimeType(resourcePath);
        HttpHeader headers = new HttpHeader();
        headers.add("Content-Type", mimeType);

        String s = SimpleTemplateEngine.readTemplate(resourcePath);
        String templateHtml = SimpleTemplateEngine.processTemplate(s, Map.of("holder", sb.toString()));
        logger.debug("동적 페이지를 반환합니다.");

        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.OK, headers, templateHtml);
    }

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
