package codesquad.database;

import codesquad.application.domain.User;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserDatabase {
    private UserDatabase() {}
    private static final List<User> userDb = new CopyOnWriteArrayList<>();

    public static void addUser(User user) {
        if (userDb.contains(user)) {
            return;
        }
        userDb.add(user);
    }

    public static List<User> findAll() {
        return Collections.unmodifiableList(userDb);
    }

    public static User getUserByIdAndPassword(String userId, String password) {
        return userDb.stream()
                .filter(user -> user.getName().equals(userId) && user.getPassword().equals(password))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static void clear() {
        userDb.clear();
    }
}
