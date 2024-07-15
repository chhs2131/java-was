package codesquad.webserver.filter;

import codesquad.application.domain.User;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.type.Cookie;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import java.util.Optional;

public class SessionFilter implements PreOnlyFilter {
    private static final SessionManager sessionManager = new SessionManager();

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public HttpRequest preFilter(final HttpRequest request) {
        // ThreadLocal 초기화
        AuthenticationHolder.clear();

        // 세션 정보 가져오기
        Cookie cookies = request.headers().getCookies();
        String sessionId = cookies.get("SID");

        // 정상 세션이 없는 경우 무시
        if (sessionId == null || !sessionManager.validSession(sessionId)) {
            return request;
        }

        // 정상인 경우 ThreadLocal에 이를 저장
        Optional<Session> session = sessionManager.getSession(sessionId);
        User user = (User) session.get().attributes().get("user");
        AuthenticationHolder.setContext(user);

        return request;
    }
}
