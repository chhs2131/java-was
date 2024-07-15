package codesquad.application.handler;

import codesquad.application.domain.Article;
import codesquad.application.domain.User;
import codesquad.database.ArticleDatabase;
import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.file.FileHttpResponseCreator;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ArticleHandler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleHandler.class);

    @RequestMapping(method = HttpMethod.GET, path = "/article/form")
    public HttpResponse getForm(HttpRequest request) {
        final User context = AuthenticationHolder.getContext();
        if (context == null) {
            logger.debug("세션 정보가 없습니다.");
            return HttpResponse.found("/user/login_failed.html", null);
        }
        return FileHttpResponseCreator.create("/article/index.html");
    }

    @RequestMapping(method = HttpMethod.POST, path = "/article/write")
    public HttpResponse writeArticle(HttpRequest request) {
        final User context = AuthenticationHolder.getContext();
        if (context == null) {
            logger.debug("세션 정보가 없습니다.");
            return HttpResponse.found("/user/login_failed.html", null);
        }

        final String title = request.body().get("title");
        final String content = request.body().get("content");

        final Article article = new Article(title, content);
        ArticleDatabase.add(article);

        return HttpResponse.found("/", "글쓰기 성공!");
    }
}
