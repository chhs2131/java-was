package codesquad.database;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JdbcConnectorTest {
    @Test
    public void test() {
        final JdbcConnector jdbcConnector = new JdbcConnector();
        jdbcConnector.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(255))");
        jdbcConnector.execute("INSERT INTO users (id, name) VALUES (1, 'Alice'), (2, 'Bob')");
        jdbcConnector.executeQuery("SELECT * FROM users");
    }
}
