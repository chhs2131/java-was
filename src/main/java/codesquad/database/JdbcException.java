package codesquad.database;

public class JdbcException extends RuntimeException {
    public JdbcException(Throwable e) {
        super(e);
    }
}
