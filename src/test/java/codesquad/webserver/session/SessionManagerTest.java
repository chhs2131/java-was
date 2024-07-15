package codesquad.webserver.session;

import codesquad.application.domain.User;
import codesquad.database.SessionDatabase;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    private SessionManager sessionManager;
    private User testUser;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
        testUser = new User("testUser", "testPass", "testNick", "test@example.com");
        SessionDatabase.clear(); // Assuming you have a method to clear the session database for testing
    }

    @Test
    @DisplayName("세션을 성공적으로 생성합니다.")
    void testCreateSession() {
        Session session = sessionManager.createSession(testUser);
        assertNotNull(session);
        assertEquals(testUser, session.attributes().get("user"));
        assertTrue(sessionManager.existsSession(session.id()));
    }

    @Test
    @DisplayName("세션을 성공적으로 조회합니다.")
    void testGetSession() {
        Session session = sessionManager.createSession(testUser);
        Optional<Session> retrievedSession = sessionManager.getSession(session.id());
        assertTrue(retrievedSession.isPresent());
        assertEquals(session.id(), retrievedSession.get().id());
        assertEquals(testUser, retrievedSession.get().attributes().get("user"));
    }

    @Test
    @DisplayName("세션 유효성을 성공적으로 검증합니다.")
    void testValidSession() {
        Session session = sessionManager.createSession(testUser);
        assertTrue(sessionManager.validSession(session.id()));
    }

    @Test
    @DisplayName("세션이 만료된 경우 유효성 검증에 실패합니다.")
    void testExpiredSession() {
        Session session = new Session("expiredSession", LocalDateTime.now().minusSeconds(1), new HashMap<>());
        SessionDatabase.addSession(session);
        assertFalse(sessionManager.validSession(session.id()));
    }

    @Test
    @DisplayName("존재하지 않는 세션의 유효성 검증에 실패합니다.")
    void testInvalidSession() {
        assertFalse(sessionManager.validSession("invalidSessionId"));
    }

    @Test
    @DisplayName("세션을 성공적으로 제거합니다.")
    void testRemoveSession() {
        Session session = sessionManager.createSession(testUser);
        sessionManager.removeSession(session.id());
        assertFalse(sessionManager.existsSession(session.id()));
        assertFalse(sessionManager.getSession(session.id()).isPresent());
    }

    @Test
    @DisplayName("세션에 저장된 사용자 정보를 성공적으로 조회합니다.")
    void testSessionUser() {
        Session session = sessionManager.createSession(testUser);
        Optional<Session> retrievedSession = sessionManager.getSession(session.id());
        assertTrue(retrievedSession.isPresent());

        User sessionUser = (User) retrievedSession.get().attributes().get("user");
        assertNotNull(sessionUser);
        assertEquals(testUser, sessionUser);
    }
}
