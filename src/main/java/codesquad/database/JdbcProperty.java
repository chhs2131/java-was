package codesquad.database;

import codesquad.webserver.annotation.Component;

@Component
public class JdbcProperty {
    private final String jdbcUrl = "jdbc:h2:mem:was;DB_CLOSE_DELAY=-1";  // 메모리 DB 삭제하지 않음
    private final String jdbcUser = "sa";
    private final String jdbcPassword = "";

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }
}
