package codesquad.database;

import codesquad.webserver.annotation.Component;

import java.sql.*;
import java.util.List;
import java.util.function.Function;

@Component
public class JdbcConnector {
    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPassword;

    public JdbcConnector(JdbcProperty jdbcProperty) {
        this.jdbcUrl = jdbcProperty.getJdbcUrl();
        this.jdbcUser = jdbcProperty.getJdbcUser();
        this.jdbcPassword = jdbcProperty.getJdbcPassword();
    }

    public void execute(String query) {
        try (
                Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
                Statement statement = connection.createStatement()
        ) {
            statement.execute(query);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    public void execute(String query, List<String> values) {
        try (
                Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
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
                Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
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
                Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
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
