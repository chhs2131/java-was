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
        if (sessionDb.containsKey(sessionId)) {
            return Optional.of(sessionDb.get(sessionId));
        }
        return Optional.empty();
    }

    public static boolean existsSession(String sessionId) {
        return getSession(sessionId).isPresent();
    }

    public static void removeSession(String sessionId) {
        if (existsSession(sessionId)) {
            sessionDb.remove(sessionId);
        }
    }

    public static void clear() {
        sessionDb.clear();
    }
}
