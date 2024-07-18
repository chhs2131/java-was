package codesquad.database.h2;

import codesquad.application.dao.CommentDao;
import codesquad.application.domain.Comment;
import codesquad.database.JdbcConnector;
import codesquad.database.JdbcException;
import codesquad.webserver.annotation.Primary;
import codesquad.webserver.annotation.Repository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class CommentH2 implements CommentDao {
    private final JdbcConnector jdbcConnector;

    public CommentH2(final JdbcConnector jdbcConnector) {
        this.jdbcConnector = jdbcConnector;

        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS comment (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                articleId BIGINT NOT NULL,
                name VARCHAR(255) NOT NULL,
                content TEXT NOT NULL
            )
            """;
        jdbcConnector.execute(createTableQuery);
    }

    @Override
    public void add(final Comment comment) {
        jdbcConnector.execute("INSERT INTO comment (articleId, name, content) VALUES (?, ?, ?)",
        List.of(String.valueOf(comment.articleId()), comment.name(), comment.content()));
    }

    @Override
    public Optional<Comment> findById(final long id) {
        return jdbcConnector.executeQuery("SELECT id, articleId, name, content FROM comment WHERE id = ?",
            List.of(String.valueOf(id)),
            resultSet -> {
                try {
                    if (resultSet.next()) {
                        Long commentId = resultSet.getLong("id");
                        Long articleId = resultSet.getLong("articleId");
                        String name = resultSet.getString("name");
                        String content = resultSet.getString("content");
                        return Optional.of(new Comment(commentId, articleId, name, content));
                    }
                    return Optional.empty();
                } catch (SQLException e) {
                    throw new JdbcException(e);
                }
            }
        );
    }

    @Override
    public List<Comment> findAllByArticleId(final long articleId) {
        return jdbcConnector.executeQuery("SELECT id, articleId, name, content FROM comment WHERE articleId = ?",
            List.of(String.valueOf(articleId)),
            resultSet -> {
            List<Comment> comments = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    final long id = resultSet.getLong("id");
                    final long artId = resultSet.getLong("articleId");
                    final String name = resultSet.getString("name");
                    final String content = resultSet.getString("content");
                    comments.add(new Comment(id, artId, name, content));
                }
            } catch (SQLException e) {
                throw new JdbcException(e);
            }
            return comments;
            });
    }

    @Override
    public void clear() {
        jdbcConnector.execute("TRUNCATE TABLE comment");
        jdbcConnector.execute("ALTER TABLE comment ALTER COLUMN id RESTART WITH 1");  // TODO 초기화 왜 안되냐..?
    }
}
