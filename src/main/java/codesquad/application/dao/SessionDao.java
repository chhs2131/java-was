package codesquad.application.dao;

import codesquad.webserver.session.Session;

import java.util.Optional;

public interface SessionDao {
    void add(Session session);
    Optional<Session> findById(String sessionId);
    boolean existsById(String sessionId);
    void removeById(String sessionId);
    void clear();
}
