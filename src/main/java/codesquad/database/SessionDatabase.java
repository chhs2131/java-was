package codesquad.database;

import codesquad.webserver.session.Session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionDatabase {
    private SessionDatabase() {}
    private static final Map<String, Session> sessionDb = new ConcurrentHashMap<>();

    public static void addSession(Session session) {
        sessionDb.put(session.id(), session);
    }

    public static Optional<Session> getSession(String sessionId) {
        return Optional.ofNullable(sessionDb.get(sessionId));
    }

    public static boolean existsSession(String sessionId) {
        return getSession(sessionId).isPresent();
    }

    public static void removeSession(String sessionId) {
        if (existsSession(sessionId)) {
            sessionDb.remove(sessionId);
        }
    }
}
