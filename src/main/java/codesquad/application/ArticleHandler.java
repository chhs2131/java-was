package codesquad.application;

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
        logger.debug("글쓰기 요청 도착! {}", request);
        return HttpResponse.found("/", "글쓰기 성공!");
    }
}
