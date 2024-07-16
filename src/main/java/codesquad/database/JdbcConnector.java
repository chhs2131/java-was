package codesquad.database;

import java.sql.*;
import java.util.List;
import java.util.function.Function;

public class JdbcConnector {
    private static final String JDBC_URL = "jdbc:h2:mem:was;DB_CLOSE_DELAY=-1";  // 메모리 DB 삭제하지 않음
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    public JdbcConnector() {}

    public JdbcConnector(String url, String id, String password) {
        // TODO 관련 정보를 외부에서 주입받기
    }

    public void execute(String query) {
        try (
        Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        Statement statement = connection.createStatement();
        ) {
            statement.execute(query);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    public void execute(String query, List<String> values) {
        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            for (int i = 0; i < values.size(); i++) {
                preparedStatement.setString(i + 1, values.get(i));
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    public <T> T executeQuery(String query, Function<ResultSet, T> mapper) {
        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
        ) {
            return mapper.apply(resultSet);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    public <T> T executeQuery(String query, List<String> values, Function<ResultSet, T> mapper) {
        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            for (int i = 0; i < values.size(); i++) {
                preparedStatement.setString(i + 1, values.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return mapper.apply(resultSet);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    public void clear() {
        // TODO 데이터베이스 전체를 초기화하기
    }
}
