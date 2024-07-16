package codesquad.database.h2;

import codesquad.application.dao.UserDao;
import codesquad.application.domain.User;
import codesquad.database.JdbcConnector;
import codesquad.database.JdbcException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserH2 implements UserDao {
    private final JdbcConnector jdbcConnector;

    public UserH2(JdbcConnector jdbcConnector) {
        this.jdbcConnector = jdbcConnector;

        // TODO Table 생성 구문 외부로 분리 필요
        String createTableQuery = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    nickname VARCHAR(255) NOT NULL
                );
                """;
        jdbcConnector.execute(createTableQuery);
    }

    @Override
    public void add(User user) {
        jdbcConnector.execute("INSERT INTO users (name, password, email, nickname) VALUES (?, ?, ?, ?)",
                List.of(user.getName(), user.getPassword(), user.getEmail(), user.getNickname()));
    }

    @Override
    public List<User> findAll() {
        return jdbcConnector.executeQuery("SELECT name, password, email, nickname FROM users",
                resultSet -> {
                    List<User> users = new ArrayList<>();
                    try {
                        while (resultSet.next()) {
                            String name = resultSet.getString("name");
                            String password = resultSet.getString("password");
                            String email = resultSet.getString("email");
                            String nickname = resultSet.getString("nickname");
                            users.add(new User(name, password, nickname, email));
                        }
                    } catch (SQLException e) {
                        throw new JdbcException(e);
                    }
                    return users;
                });
    }

    @Override
    public Optional<User> getUserByIdAndPassword(String userId, String userPassword) {
        User user = jdbcConnector.executeQuery("SELECT name, password, email, nickname FROM users WHERE name LIKE ? AND password = ?",
                List.of(userId, userPassword),
                resultSet -> {
                    try {
                        if (!resultSet.next()) {
                            return null;
                        }
                        String name = resultSet.getString("name");
                        String password = resultSet.getString("password");
                        String email = resultSet.getString("email");
                        String nickname = resultSet.getString("nickname");
                        return new User(name, password, nickname, email);
                    } catch (SQLException e) {
                        throw new JdbcException(e);
                    }
                });

        return Optional.ofNullable(user);
    }

    @Override
    public void clear() {
        jdbcConnector.execute("TRUNCATE TABLE users");
    }
}
