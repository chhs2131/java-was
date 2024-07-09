package codesquad.webserver.session;

import codesquad.database.SessionDatabase;
import codesquad.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    public Session getSession(String sessionId) {
        // TODO 없는 경우 throw?
        return SessionDatabase.getSession(sessionId).get();
    }

    public Session createSession(User user) {
        // 세션 생성
        String sessionId = createUniqueSessionId();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user", user);

        Session session = new Session(sessionId, LocalDateTime.MAX, attributes);

        // 디비에 추가
        SessionDatabase.addSession(session);
        return session;
    }

    public boolean existsSession(String sessionId) {
        return SessionDatabase.existsSession(sessionId);
    }

    private String createUniqueSessionId() {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (existsSession(uuid));

        return uuid;
    }
}
