package codesquad.application.dao;

import codesquad.application.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    void add(User user);
    List<User> findAll();
    Optional<User> getUserByIdAndPassword(String userId, String password);
    void clear();
}
