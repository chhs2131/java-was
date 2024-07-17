package codesquad.application;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.application.dao.UserDao;
import codesquad.application.handler.UserHandler;
import codesquad.application.domain.User;
import codesquad.database.JdbcConnector;
import codesquad.database.JdbcProperty;
import codesquad.database.h2.UserH2;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.http.type.HttpProtocol;
import codesquad.webserver.http.type.HttpStatus;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserHandlerTest {
    private UserHandler userHandler;
    private SessionManager sessionManager;
    private User testUser;
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        AuthenticationHolder.clear();
        sessionManager = new SessionManager();
        testUser = new User("testUser", "testPass", "testNick", "test@example.com");

        userDao = new UserH2(new JdbcConnector(new JdbcProperty()));
        userHandler = new UserHandler(userDao);
        userDao.clear();  // UserDatabase 초기화
        sessionManager.clear();
        AuthenticationHolder.clear();  // AuthenticationHolder 초기화
    }

    @Test
    @DisplayName("유저 생성 요청을 성공적으로 수행한다.")
    public void test_create_user_success() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "testUser");
        requestBody.put("password", "testPass");
        requestBody.put("nickname", "testNick");
        requestBody.put("email", "test@example.com");
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/user/create", new HashMap<>(), HttpProtocol.HTTP_1_1, HttpHeader.createEmpty(), requestBody);

        HttpResponse response = userHandler.createUser(httpRequest);

        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals("/", response.getHeaders().get("Location"));
        assertEquals("유저가 생성되었습니다.", response.getBody());
    }

    @Test
    @DisplayName("유저 생성에 필요한 값이 부족한 경우 IllegalArgumentException이 발생합니다.")
    public void test_create_user_missing_fields() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "testUser");  // password, nickname, email fields 없음
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/user/create", new HashMap<>(), HttpProtocol.HTTP_1_1, HttpHeader.createEmpty(), requestBody);

        assertThrows(IllegalArgumentException.class, () -> {
            userHandler.createUser(httpRequest);
        });
    }

    @Test
    @DisplayName("유저 목록 요청을 성공적으로 처리합니다.")
    void testGetUserList() {
        // UserDatabase에 유저 추가
        userDao.add(testUser);

        // 세션 생성 및 설정
        Session session = sessionManager.createSession(testUser);
        AuthenticationHolder.setContext(testUser);

        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", "SID=" + session.id());

        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/user/list", headers, HttpProtocol.HTTP_1_1, HttpHeader.createEmpty(), new HashMap<>());

        HttpResponse response = userHandler.getUserList(httpRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().contains(testUser.getName()));
        assertTrue(response.getBody().contains(testUser.getEmail()));
        assertTrue(response.getBody().contains(testUser.getNickname()));
    }

    @Test
    @DisplayName("로그인되지 않은 사용자의 유저 목록 요청을 리다이렉트합니다.")
    void testGetUserListWithoutLogin() {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/user/list", new HashMap<>(), HttpProtocol.HTTP_1_1, HttpHeader.createEmpty(), new HashMap<>());

        HttpResponse response = userHandler.getUserList(httpRequest);

        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals("/user/login_failed.html", response.getHeaders().get("Location"));
    }
}
