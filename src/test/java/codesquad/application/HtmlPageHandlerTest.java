package codesquad.application;

import codesquad.application.dao.UserDao;
import codesquad.application.handler.HtmlPageHandler;
import codesquad.application.domain.User;
import codesquad.database.JdbcConnector;
import codesquad.database.JdbcProperty;
import codesquad.database.h2.ArticleH2;
import codesquad.database.java.UserDatabase;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.http.type.HttpProtocol;
import codesquad.webserver.http.type.HttpStatus;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HtmlPageHandlerTest {
    private HtmlPageHandler htmlPageHandler;
    private SessionManager sessionManager;
    private User testUser;
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        AuthenticationHolder.clear();
        sessionManager = new SessionManager();
        testUser = new User("testUser", "testPass", "testNick", "test@example.com");

        htmlPageHandler = new HtmlPageHandler(new ArticleH2(new JdbcConnector(new JdbcProperty())));
        userDao = new UserDatabase();
        userDao.clear();  // UserDatabase 초기화
        sessionManager.clear();
        AuthenticationHolder.clear();  // AuthenticationHolder 초기화
    }

    @Test
    @DisplayName("홈페이지 요청을 성공적으로 처리합니다.")
    void testGetHomepage() {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/", new HashMap<>(), HttpProtocol.HTTP_1_1, HttpHeader.createEmpty(), new HashMap<>());

        HttpResponse response = htmlPageHandler.getHomepage(httpRequest);

        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.body().contains("로그인"));
    }

    @Test
    @DisplayName("로그인된 사용자의 홈페이지 요청을 성공적으로 처리합니다.")
    void testGetHomepageWithLoggedInUser() {
        // UserDatabase에 유저 추가
        userDao.add(testUser);

        // 세션 생성 및 설정
        Session session = sessionManager.createSession(testUser);
        AuthenticationHolder.setContext(testUser);

        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", "SID=" + session.id());

        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/", headers, HttpProtocol.HTTP_1_1, HttpHeader.createEmpty(), new HashMap<>());

        HttpResponse response = htmlPageHandler.getHomepage(httpRequest);

        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.body().contains(testUser.getName() + "님 환영합니다."));
    }
}