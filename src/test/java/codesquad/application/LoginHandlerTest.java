package codesquad.application;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.database.SessionDatabase;
import codesquad.database.UserDatabase;
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

class LoginHandlerTest {
    @BeforeEach
    public void setUp() {
        UserDatabase.clear();
        SessionDatabase.clear();
        AuthenticationHolder.clear();
    }

    @Test
    @DisplayName("로그인을 성공적으로 수행한다.")
    public void test_login_success() {
        User user = new User("testUser", "testPass", "testNick", "test@example.com");
        UserDatabase.addUser(user);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "testUser");
        requestBody.put("password", "testPass");
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/user/login", new HashMap<>(), HttpProtocol.HTTP_1_1, HttpHeader.createEmpty(), requestBody);

        HttpResponse response = LoginHandler.login(httpRequest);

        assertEquals(HttpStatus.FOUND, response.status());
        assertEquals("/main/index.html", response.headers().get("Location"));
        assertTrue(response.headers().contains("Set-Cookie"));
        assertEquals("로그인 완료!", response.body());
    }

    @Test
    @DisplayName("인증 정보가 잘못된 경우 로그인에 실패합니다.")
    public void test_login_failure() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "wrongUser");
        requestBody.put("password", "wrongPass");
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/user/login", new HashMap<>(), HttpProtocol.HTTP_1_1, HttpHeader.createEmpty(), requestBody);

        HttpResponse response = LoginHandler.login(httpRequest);

        assertEquals(HttpStatus.FOUND, response.status());
        assertEquals("/user/login_failed.html", response.headers().get("Location"));
    }

    @Test
    @DisplayName("로그아웃을 성공적으로 수행합니다.")
    public void test_logout_success() {
        User user = new User("testUser", "testPass", "testNick", "test@example.com");
        UserDatabase.addUser(user);

        // Assume login process and session creation
        final SessionManager sessionManager = new SessionManager();
        final Session session = sessionManager.createSession(user);
        String sessionId = session.id();
        AuthenticationHolder.setContext(user);

        final HttpHeader cookie = HttpHeader.of("Cookie", "SID=" + sessionId);
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/user/logout", new HashMap<>(), HttpProtocol.HTTP_1_1, cookie, new HashMap<>());

        HttpResponse response = LoginHandler.logout(httpRequest);

        assertEquals(HttpStatus.FOUND, response.status());
        assertEquals("/index.html", response.headers().get("Location"));
        assertFalse(sessionManager.validSession(sessionId));
    }

    @Test
    @DisplayName("세션이 만료된 경우 로그아웃을 실패(401)합니다.")
    public void test_logout_session_expired_failure() {
        User user = new User("testUser", "testPass", "testNick", "test@example.com");
        UserDatabase.addUser(user);

        // Assume login process and session creation, then session expiration
        final SessionManager sessionManager = new SessionManager();
        final Session session = sessionManager.createSession(user);
        String sessionId = session.id();

        // Manually expire the session
        sessionManager.removeSession(sessionId);

        final HttpHeader cookie = HttpHeader.of("Cookie", "SID=" + sessionId);
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/user/logout", new HashMap<>(), HttpProtocol.HTTP_1_1, cookie, new HashMap<>());

        HttpResponse response = LoginHandler.logout(httpRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.status());
        assertEquals("세션이 존재하지 않습니다.", response.body());
    }

    @Test
    @DisplayName("해당하는 세션이 없는 경우 로그아웃을 실패(401)합니다.")
    public void test_logout_have_not_session_failure() {
        User user = new User("testUser", "testPass", "testNick", "test@example.com");
        UserDatabase.addUser(user);

        String invalidSessionId = "invalid-session-id";
        final HttpHeader cookie = HttpHeader.of("Cookie", "SID=" + invalidSessionId);
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/user/logout", new HashMap<>(), HttpProtocol.HTTP_1_1, cookie, new HashMap<>());

        HttpResponse response = LoginHandler.logout(httpRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.status());
        assertEquals("세션이 존재하지 않습니다.", response.body());
    }
}