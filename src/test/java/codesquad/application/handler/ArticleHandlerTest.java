package codesquad.application.handler;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ArticleHandlerTest {
    private ArticleHandler articleHandler;
    private ArticleDao articleDao;
    private CommentDao commentDao;
    private JdbcConnector jdbcConnector;

    @BeforeEach
    public void setUp() {
        jdbcConnector = new JdbcConnector(new JdbcProperty());
        articleDao = new ArticleH2(jdbcConnector);
        commentDao = new CommentH2(jdbcConnector);
        articleHandler = new ArticleHandler(articleDao, commentDao);

        AuthenticationHolder.clear();
        articleDao.clear();
        commentDao.clear();
    }

    @Test
    @DisplayName("인증 정보가 존재하면 글쓰기 페이지에 정상 진입한다. (OK)")
    public void testGetFormWithAuthenticatedUser() {
        AuthenticationHolder.setContext(new User("testUser", "password", "Test User", "email@email.com"));
        HttpResponse response = articleHandler.getForm(null);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    @DisplayName("인증 정보가 없다면 글쓰기 페이지로 접근할 수 없다. (FOUND)")
    public void testGetFormWithoutAuthenticatedUser() {
        HttpResponse response = articleHandler.getForm(null);
        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("글쓰기에 성공한다.")
    public void testWriteArticleWithAuthenticatedUser() {
        AuthenticationHolder.setContext(new User("testUser", "password", "Test User", "email@email.com"));

        final HttpRequest request = new HttpRequest(
            HttpMethod.GET,
            "/article/write",
            Map.of(),
            HttpProtocol.HTTP_1_1,
            HttpHeader.createEmpty(),
            Map.of("title", "제목입니다", "content", "글 내용입니다", "image", "helloworld")
        );

        HttpResponse response = articleHandler.writeArticle(request);
        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals("글쓰기 성공!", response.getBody());
        Article addedArticle = articleDao.get(1L).orElseThrow();
        assertEquals("제목입니다", addedArticle.title());
        assertEquals("글 내용입니다", addedArticle.content());
    }

    @Test
    @DisplayName("로그인하지 않으면 글쓰기에 실패한다.")
    public void testWriteArticleWithoutAuthenticatedUser() {
        final HttpRequest request = new HttpRequest(
            HttpMethod.GET,
            "/article/write",
            Map.of(),
            HttpProtocol.HTTP_1_1,
            HttpHeader.createEmpty(),
            Map.of("title", "제목입니다", "content", "글 내용입니다")
        );

        HttpResponse response = articleHandler.writeArticle(request);
        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals("/user/login_failed.html", response.getHeaders().get("Location"));
    }

    // TODO
//    @Test
//    @DisplayName("게시글이 존재하는 경우 정상 조회한다.")
//    public void testGetArticle() {
//        Article article = new Article(null, "정상조회제목", "정상조회내용");
//        articleDao.add(article);
//
//        final HttpRequest request = new HttpRequest(
//            HttpMethod.GET,
//            "/article",
//            Map.of("id", "1"),
//            HttpProtocol.HTTP_1_1,
//            HttpHeader.createEmpty(),
//            Map.of()
//        );
//
//        HttpResponse response = articleHandler.get(request);
//        assertEquals(HttpStatus.OK, response.status());
//    }

    @Test
    @DisplayName("게시글이 존재하지 않는 경우 예외가 발생한다.")
    public void testGetArticle_failed() {
        final HttpRequest request = new HttpRequest(
            HttpMethod.GET,
            "/article",
            Map.of("id", "1"),
            HttpProtocol.HTTP_1_1,
            HttpHeader.createEmpty(),
            Map.of()
        );

        assertThrows(IllegalArgumentException.class, () -> articleHandler.get(request));
    }
}
