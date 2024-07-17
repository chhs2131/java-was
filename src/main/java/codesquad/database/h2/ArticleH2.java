package codesquad.database.h2;

import codesquad.application.dao.ArticleDao;
import codesquad.application.domain.Article;
import codesquad.database.JdbcConnector;
import codesquad.database.JdbcException;
import codesquad.webserver.annotation.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ArticleH2 implements ArticleDao {
    private final JdbcConnector jdbcConnector;

    public ArticleH2(JdbcConnector jdbcConnector) {
        this.jdbcConnector = jdbcConnector;

        // TODO Table 생성 구문 외부로 분리 필요
        String createTableQuery = """
                    CREATE TABLE IF NOT EXISTS article (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        content TEXT NOT NULL,
                        imagePath VARCHAR(255) NOT NULL
                    );
                """;
        jdbcConnector.execute(createTableQuery);
    }

    @Override
    public void add(Article article) {
        jdbcConnector.execute("INSERT INTO article (title, content, imagePath) VALUES (?, ?, ?)",
                List.of(article.title(), article.content(), article.imagePath()));
    }

    @Override
    public List<Article> findAll() {
        return jdbcConnector.executeQuery("SELECT id, title, content, imagePath FROM article",
                resultSet -> {
                    List<Article> articles = new ArrayList<>();
                    try {
                        while (resultSet.next()) {
                            Long id = resultSet.getLong("id");
                            String title = resultSet.getString("title");
                            String content = resultSet.getString("content");
                            String imagePath = resultSet.getString("imagePath");
                            articles.add(new Article(id, title, content, imagePath));
                        }
                    } catch (SQLException e) {
                        throw new JdbcException(e);
                    }
                    return articles;
                });
    }

    @Override
    public Optional<Article> get(long id) {
        return jdbcConnector.executeQuery("SELECT id, title, content, imagePath FROM article WHERE id = ?",
                List.of(String.valueOf(id)),
                resultSet -> {
                    try {
                        if (resultSet.next()) {
                            Long articleId = resultSet.getLong("id");
                            String title = resultSet.getString("title");
                            String content = resultSet.getString("content");
                            String imagePath = resultSet.getString("imagePath");
                            return Optional.of(new Article(articleId, title, content, imagePath));
                        }
                        return Optional.empty();
                    } catch (SQLException e) {
                        throw new JdbcException(e);
                    }
                });
    }

    @Override
    public boolean existsById(final long id) {
        return get(id).isPresent();
    }

    @Override
    public void clear() {
        jdbcConnector.execute("TRUNCATE TABLE article");
    }
}
