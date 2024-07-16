package codesquad.webserver.session;

import codesquad.application.dao.SessionDao;
import codesquad.database.SessionDatabase;
import codesquad.application.domain.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {
    private static final int SESSION_MAX_AGE = 3600;
    private final SessionDao sessionDao = new SessionDatabase();

    public Optional<Session> getSession(String sessionId) {
        return sessionDao.findById(sessionId);
    }

    public boolean validSession(String sessionId) {
        Optional<Session> sessionOpt = getSession(sessionId);
        if (sessionOpt.isEmpty()) {
            return false;
        }

        Session session = sessionOpt.get();
        return LocalDateTime.now().isBefore(session.expired());
    }

    public Session createSession(User user) {
        // 세션 생성
        String sessionId = createUniqueSessionId();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user", user);

        Session session = new Session(sessionId, LocalDateTime.now().plusSeconds(SESSION_MAX_AGE), attributes);

        // 디비에 추가
        sessionDao.add(session);
        return session;
    }

    public void addSession(Session session) {
        sessionDao.add(session);
    }

    public void removeSession(String sessionId) {
        sessionDao.removeById(sessionId);
    }

    public boolean existsSession(String sessionId) {
        return sessionDao.existsById(sessionId);
    }

    public void clear() {
        sessionDao.clear();
    }

    private String createUniqueSessionId() {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (existsSession(uuid));

        return uuid;
    }
}
