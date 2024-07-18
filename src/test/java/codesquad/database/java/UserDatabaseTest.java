package codesquad.database.java;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.application.domain.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserDatabaseTest {
    private UserDatabase  userDatabase;

    @BeforeEach
    public void setUp() {
        userDatabase = new UserDatabase();
    }

    @Test
    @DisplayName("User를 정상적으로 추가합니다.")
    public void test_adding_new_user() {
        User user = new User("John", "password123", "johnny", "john@example.com");

        userDatabase.add(user);

        List<User> users = userDatabase.findAll();
        assertTrue(users.contains(user));
    }

    @Test
    @DisplayName("Null을 추가하려하면 예외가 발생합니다.")
    public void test_adding_null_user() {
        assertThrows(IllegalArgumentException.class, () -> {
            userDatabase.add(null);
        });
    }

    @Test
    @DisplayName("아이디 패스워드에 해당하는 유저를 찾아서 반환합니다.")
    public void test_get_user_by_id_and_password() {
        User user = new User("John", "password123", "johnny", "john@example.com");
        userDatabase.add(user);

        final Optional<User> theUser = userDatabase.getUserByIdAndPassword("John", "password123");

        assertEquals(user, theUser.get());
    }

    @Test
    @DisplayName("아이디 패스워드에 해당하는 유저가 없는 경우 Optional로 wrapping된 빈값이 반환됩니다.")
    public void test_get_user_by_id_and_password2() {
        User user = new User("John", "password123", "johnny", "john@example.com");
        userDatabase.add(user);

        final Optional<User> theUser = userDatabase.getUserByIdAndPassword("Notme", "password123");

        assertFalse(theUser.isPresent());
    }
}
