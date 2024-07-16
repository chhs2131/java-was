package codesquad.application.handler;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import codesquad.application.dao.ArticleDao;
import codesquad.application.dao.CommentDao;
import codesquad.application.domain.Article;
import codesquad.application.domain.User;
import codesquad.database.JdbcConnector;
import codesquad.database.JdbcProperty;
import codesquad.database.h2.ArticleH2;
import codesquad.database.h2.CommentH2;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.http.type.HttpProtocol;
import codesquad.webserver.http.type.HttpStatus;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentHandlerTest {
    private CommentHandler commentHandler;
    private CommentDao commentDao;
    private ArticleDao articleDao;

    @BeforeEach
    public void setUp() {
        JdbcConnector jdbcConnector = new JdbcConnector(new JdbcProperty());
        commentDao = new CommentH2(jdbcConnector);
        articleDao = new ArticleH2(jdbcConnector);
        commentHandler = new CommentHandler(commentDao, articleDao);

        AuthenticationHolder.clear();
        commentDao.clear();
        articleDao.clear();
    }

    @Test
    @DisplayName("게시글에 댓글을 추가한다.")
    public void add_comment() {
        AuthenticationHolder.setContext(createTestUser());
        Article article = new Article(null, "정상조회제목", "정상조회내용");
        articleDao.add(article);

        final Long id = articleDao.findAll().get(0).id();// TODO DB AUTO_INCREMENT CleanUp이 잘될때 제거..
        final HttpRequest request = new HttpRequest(
            HttpMethod.POST,
            "/article/comment",
            Map.of("id", String.valueOf(id)),
            HttpProtocol.HTTP_1_1,
            HttpHeader.createEmpty(),
            Map.of("articleId", String.valueOf(id), "content", "hi")
        );
        final HttpResponse httpResponse = commentHandler.writeComment(request);

        assertEquals(HttpStatus.FOUND, httpResponse.status());
        assertThat(httpResponse.headers().get("Location")).startsWith("/article?");
    }

    @Test
    @DisplayName("존재하지 않는 글에 댓글을 추가할 수 없으며 IllegalArguemtnException이 발생한다.")
    public void add_comment_to_non_existing_article() {
        AuthenticationHolder.setContext(createTestUser());

        final HttpRequest request = new HttpRequest(
            HttpMethod.POST,
            "/article/comment",
            Map.of("id", "12345"),  // 존재하지 않는 게시글 ID
            HttpProtocol.HTTP_1_1,
            HttpHeader.createEmpty(),
            Map.of("articleId", "12345", "content", "댓글 내용")
        );

        assertThrows(IllegalArgumentException.class, () -> commentHandler.writeComment(request));
    }

    @Test
    @DisplayName("로그인하지 않은 상태에서 댓글을 추가할 수 없다.")
    public void add_comment_without_authentication() {
        final HttpRequest request = new HttpRequest(
            HttpMethod.POST,
            "/article/comment",
            Map.of("id", "1"),
            HttpProtocol.HTTP_1_1,
            HttpHeader.createEmpty(),
            Map.of("articleId", "1", "content", "댓글 내용")
        );

        final HttpResponse httpResponse = commentHandler.writeComment(request);
        assertThat(httpResponse.status()).isEqualTo(HttpStatus.FOUND);
        assertThat(httpResponse.headers().get("Location")).startsWith("/user/login_failed.html");
    }

    private User createTestUser() {
        return new User("testUser", "password", "Test User", "email@email.com");
    }
}