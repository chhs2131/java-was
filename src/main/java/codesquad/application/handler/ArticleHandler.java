package codesquad.application.handler;

import codesquad.application.dao.ArticleDao;
import codesquad.application.domain.Article;
import codesquad.application.domain.User;
import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.file.FileHttpResponseCreator;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Controller
public class ArticleHandler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleHandler.class);
    private final ArticleDao articleDao;

    public ArticleHandler(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

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

        final Article article = new Article(null, title, content);
        articleDao.add(article);

        return HttpResponse.found("/", "글쓰기 성공!");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/article")
    public HttpResponse get(HttpRequest request) {
        final Long id = Long.valueOf(request.queryString().get("id"));
        if (id < 0) throw new IllegalArgumentException("id값이 잘못되었습니다. " + id);

        Article article = articleDao.get(id)
                .orElseThrow(IllegalArgumentException::new);

        return FileHttpResponseCreator.create("/article/article.html", Map.of(
                "title", article.title(),
                "content", article.content()
        ));
    }
}
