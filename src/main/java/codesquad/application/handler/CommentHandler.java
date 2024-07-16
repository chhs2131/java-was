package codesquad.application.handler;

import codesquad.application.dao.CommentDao;
import codesquad.application.domain.Comment;
import codesquad.application.domain.User;
import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CommentHandler {
    private static final Logger logger = LoggerFactory.getLogger(CommentHandler.class);
    private final CommentDao commentDao;

    public CommentHandler(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @RequestMapping(method = HttpMethod.POST, path = "/article/comment")
    public HttpResponse writeComment(HttpRequest httpRequest) {
        final User context = AuthenticationHolder.getContext();
        if (context == null) {
            logger.debug("세션 정보가 없습니다.");
            return HttpResponse.found("/user/login_failed.html", null);
        }

        final Long articleId = Long.parseLong(httpRequest.body().get("articleId"));
        final String content = httpRequest.body().get("content");

        final Comment comment = new Comment(null, articleId, context.getNickname(), content);
        commentDao.add(comment);

        return HttpResponse.found("/article?id=" + articleId, "댓글이 성공적으로 생성되었습니다.");
    }
}
