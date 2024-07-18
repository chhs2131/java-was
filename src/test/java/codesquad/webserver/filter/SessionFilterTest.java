package codesquad.webserver.filter;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.application.domain.User;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.http.type.HttpProtocol;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionFilterTest {
    private SessionFilter sessionFilter;
    private SessionManager sessionManager;

    @BeforeEach
    public void setUp() {
        sessionFilter = new SessionFilter();
        sessionManager = new SessionManager();
        AuthenticationHolder.clear();
    }

    @Test
    @DisplayName("정상 세션이 존재하는 경우 인증정보를 저장합니다.")
    public void test_session_filter() {
        User user = new User("John", "password123", "johnny", "john@example.com");
        final Session session = sessionManager.createSession(user);
        HttpRequest request = new HttpRequest(
            HttpMethod.GET,
            "/",
            Map.of(),
            HttpProtocol.HTTP_1_1,
            HttpHeader.of("Cookie", "SID=" + session.id()),
            Map.of()
        );

        sessionFilter.preFilter(request);

        assertEquals(user, AuthenticationHolder.getContext());
    }

    @Test
    @DisplayName("세션이 없는 경우 인증 정보는 비어있습니다.")
    public void clears_threadlocal_context() {
        HttpRequest request = new HttpRequest(
            HttpMethod.GET,
            "/",
            Map.of(),
            HttpProtocol.HTTP_1_1,
            HttpHeader.createEmpty(),
            Map.of()
        );

        sessionFilter.preFilter(request);

        assertEquals(null, AuthenticationHolder.getContext());
    }

    @Test
    @DisplayName("세션이 만료된 경우 인증 정보를 저장하지 않습니다.")
    public void test_session_filter_expired() {
        HttpRequest request = new HttpRequest(
            HttpMethod.GET,
            "/",
            Map.of(),
            HttpProtocol.HTTP_1_1,
            HttpHeader.of("Cookie", "SID=1234567890"),
            Map.of()
        );

        sessionFilter.preFilter(request);

        assertEquals(null, AuthenticationHolder.getContext());
    }
}