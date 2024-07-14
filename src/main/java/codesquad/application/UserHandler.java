package codesquad.application;

import static codesquad.webserver.handler.FileHttpResponseCreator.create;

import codesquad.database.UserDatabase;
import codesquad.webserver.util.StringUtil;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    private UserHandler() {}

    // GET /
    public static HttpResponse getHomepage(HttpRequest httpRequest) {
        final Cookie cookies = httpRequest.headers().getCookies();
        final String sid = cookies.get("SID");

        User user = AuthenticationHolder.getContext();
        String holderValue = "";
        if (user == null) {
            logger.debug("세션이 존재하지 않습니다. sid:{}", sid);
            holderValue = "<a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>";
        } else {
            holderValue = user.getName() + "님 환영합니다.";
        }

        String resourcePath = "/index.html";
        return create(resourcePath, Map.of("holder", holderValue));
    }

    protected static String getMimeType(String staticFilePath) {
        String extension = StringUtil.getExtension(staticFilePath);
        return ContentType.from(extension).getMimeType();
    }

    // GET /user/list
    public static HttpResponse getUserList(HttpRequest httpRequest) {
        final Cookie cookies = httpRequest.headers().getCookies();
        final String sid = cookies.get("SID");

        User context = AuthenticationHolder.getContext();
        if (context == null) {
            logger.debug("세션이 존재하지 않습니다. sid:{}", sid);
            return HttpResponse.found("/user/login_failed.html", null);
        }

        // HTML 값 생성
        List<User> users = UserDatabase.findAll();
        StringBuilder sb = new StringBuilder();
        users.forEach(user -> {
            sb.append("<tr>");
            sb.append("<td>").append(user.getName()).append("</td>");
            sb.append("<td>").append(user.getEmail()).append("</td>");
            sb.append("<td>").append(user.getNickname()).append("</td>");
            sb.append("</tr>");
        });

        // HTML 페이지 불러오기
        String resourcePath = "/user/list.html";
        return create(resourcePath, Map.of("holder", sb.toString()));
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
        return HttpResponse.found("/index.html", "유저가 생성되었습니다.");
    }
}
