package codesquad.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcConnector {
    private static final String JDBC_URL = "jdbc:h2:mem:was;DB_CLOSE_DELAY=-1";  // 메모리 DB 삭제하지 않음
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    public void execute(String query) {
        try (
        Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        Statement statement = connection.createStatement();
        ) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();  // TODO throw CustomException
        }
    }

    public void executeQuery(String query) {
        try (
            Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
        ) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("ID: " + id + ", Name: " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();  // TODO throw CustomException
        }
    }

    public void clear() {
        // 데이터베이스 전체를 초기화하기
    }
}
