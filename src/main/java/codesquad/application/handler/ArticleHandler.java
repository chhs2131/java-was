package codesquad.application.handler;

import codesquad.application.dao.ArticleDao;
import codesquad.application.dao.CommentDao;
import codesquad.application.domain.Article;
import codesquad.application.domain.User;
import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.file.FileHttpResponseCreator;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.http.type.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Controller
public class ArticleHandler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleHandler.class);
    private final ArticleDao articleDao;
    private final CommentDao commentDao;

    public ArticleHandler(ArticleDao articleDao, final CommentDao commentDao) {
        this.articleDao = articleDao;
        this.commentDao = commentDao;
    }

    @RequestMapping(method = HttpMethod.GET, path = "/article/form")
    public HttpResponse getForm(HttpRequest request) {
        final User context = AuthenticationHolder.getContext();
        if (context == null) {
            logger.debug("세션 정보가 없습니다.");
            return HttpResponse.found("/login/index.html", "로그인이 필요합니다.");
        }
        return FileHttpResponseCreator.create("/article/index.html");
    }

    @RequestMapping(method = HttpMethod.POST, path = "/article/write")
    public HttpResponse writeArticle(HttpRequest request) {
        final User context = AuthenticationHolder.getContext();
        if (context == null) {
            logger.debug("세션 정보가 없습니다. (글 작성 실패)");
            return FileHttpResponseCreator.create(HttpStatus.UNAUTHORIZED, "/user/login_failed.html");
        }

        final String title = request.body().get("title");
        final String content = request.body().get("content");
        final String imagePath = request.body().get("image");

        if (title == null || content == null || imagePath == null) {
            throw new IllegalArgumentException("글에 Null값이 들어올 수 없습니다. " + title + " " + content + " " + imagePath);
        }

        final Article article = new Article(null, title, content, imagePath);
        articleDao.add(article);

        return HttpResponse.found("/", "글쓰기 성공!");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/article")
    public HttpResponse get(HttpRequest request) {
        final Long id = Long.valueOf(request.queryString().get("id"));
        if (id < 0) throw new IllegalArgumentException("id값이 잘못되었습니다. " + id);

        // 헤더 HTML 작성
        User user = AuthenticationHolder.getContext();
        String holderValue = "";
        String buttonValue = "";
        if (user == null) {
            logger.debug("세션이 존재하지 않습니다.");
            holderValue = "<a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>";
            buttonValue = "<a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>";
        } else {
            holderValue = user.getName() + "님 환영합니다.";
            buttonValue = "<a class=\"btn btn_ghost btn_size_s\" href=\"/user/logout\">로그아웃</a>";
        }

        // 게시글 작성
        Article article = articleDao.get(id)
                .orElseThrow(IllegalArgumentException::new);

        StringBuilder commentBuilder = new StringBuilder();
        commentDao.findAllByArticleId(article.id())
            .forEach(comment -> commentBuilder.append("          <li class=\"comment__item\">\n"
                + "            <div class=\"comment__item__user\">\n"
                + "              <img class=\"comment__item__user__img\" />\n"
                + "              <p class=\"comment__item__user__nickname\">\n"
            ).append(
                comment.name()
            ).append(
                "              </p>\n"
                + "            </div>\n"
                + "            <p class=\"comment__item__article\">\n"
            ).append(
                comment.content()
            ).append(
                "            </p>\n"
                + "          </li>"
            ));

        return FileHttpResponseCreator.create("/article/article.html", Map.of(
            "articleId", String.valueOf(article.id()),
            "title", article.title(),
            "content", article.content(),
            "imagePath", article.imagePath(),
            "comments", commentBuilder.toString(),
            "holder", holderValue,
            "signupOrLogoutButton", buttonValue
        ));
    }
}
