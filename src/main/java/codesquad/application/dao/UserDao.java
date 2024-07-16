package codesquad.application.dao;

import codesquad.application.domain.User;

import java.util.List;

public interface UserDao {
    void add(User user);
    List<User> findAll();
    User getUserByIdAndPassword(String userId, String password);
    void clear();
}
