package codesquad.application.handler;

import static codesquad.webserver.file.FileHttpResponseCreator.create;

import codesquad.application.dao.ArticleDao;
import codesquad.application.domain.Article;
import codesquad.application.domain.User;
import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.Cookie;
import codesquad.webserver.http.type.HttpMethod;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Controller
public class HtmlPageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HtmlPageHandler.class);
    private final ArticleDao articleDao;

    public HtmlPageHandler(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @RequestMapping(method = HttpMethod.GET, path = "/registration")
    public HttpResponse getRegistrationPage(HttpRequest httpRequest) {
        return create("/registration/index.html");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public HttpResponse getLoginPage(HttpRequest httpRequest) {
        return create("/login/index.html");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/")
    public HttpResponse getHomepage(HttpRequest httpRequest) {
        final Cookie cookies = httpRequest.headers().getCookies();
        final String sid = cookies.get("SID");

        User user = AuthenticationHolder.getContext();
        String holderValue = "";
        String buttonValue = "";
        if (user == null) {
            logger.debug("세션이 존재하지 않습니다. sid:{}", sid);
            holderValue = "<a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>";
            buttonValue = "<a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>";
        } else {
            holderValue = user.getName() + "님 환영합니다.";
            buttonValue = "<a class=\"btn btn_ghost btn_size_s\" href=\"/user/logout\">로그아웃</a>";
        }

        // article 제목 목록
        StringBuilder titles = new StringBuilder();
        final List<Article> articles = articleDao.findAll();
        articles.forEach(article -> titles.append("<p>").append(article.id() + " => " + article.title() + " " + article.content()).append("</p>"));

        String resourcePath = "/index.html";
        return create(resourcePath, Map.of("holder", holderValue, "signupOrLogoutButton", buttonValue, "articles", titles.toString()));
    }
}
