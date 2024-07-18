package codesquad.database.java;

import codesquad.application.dao.UserDao;
import codesquad.application.domain.User;

import codesquad.webserver.annotation.Repository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class UserDatabase implements UserDao {
    private static final List<User> userDb = new CopyOnWriteArrayList<>();

    public void add(User user) {
        if (user == null) throw new IllegalArgumentException("user is null");
        if (userDb.contains(user)) return;
        userDb.add(user);
    }

    public List<User> findAll() {
        return Collections.unmodifiableList(userDb);
    }

    public Optional<User> getUserByIdAndPassword(String userId, String password) {
        return userDb.stream()
                .filter(user -> user.getName().equals(userId) && user.getPassword().equals(password))
                .findFirst();
    }

    public void clear() {
        userDb.clear();
    }
}
