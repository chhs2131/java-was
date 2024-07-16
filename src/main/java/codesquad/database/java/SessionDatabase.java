package codesquad.database.java;

import codesquad.application.dao.SessionDao;
import codesquad.webserver.session.Session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionDatabase implements SessionDao {
    private static final Map<String, Session> sessionDb = new ConcurrentHashMap<>();

    public void add(Session session) {
        sessionDb.put(session.id(), session);
    }

    public Optional<Session> findById(String sessionId) {
        if (sessionDb.containsKey(sessionId)) {
            return Optional.of(sessionDb.get(sessionId));
        }
        return Optional.empty();
    }

    public boolean existsById(String sessionId) {
        return findById(sessionId).isPresent();
    }

    public void removeById(String sessionId) {
        if (existsById(sessionId)) {
            sessionDb.remove(sessionId);
        }
    }

    public void clear() {
        sessionDb.clear();
    }
}
