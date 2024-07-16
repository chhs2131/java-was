package codesquad.database.h2;

import codesquad.application.dao.ArticleDao;
import codesquad.application.domain.Article;
import codesquad.database.JdbcConnector;
import codesquad.database.JdbcException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArticleH2 implements ArticleDao {
    private final JdbcConnector jdbcConnector;

    public ArticleH2(JdbcConnector jdbcConnector) {
        this.jdbcConnector = jdbcConnector;

        // TODO Table 생성 구문 외부로 분리 필요
        String createTableQuery = """
                    CREATE TABLE IF NOT EXISTS article (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        content TEXT NOT NULL
                    );
                """;
        jdbcConnector.execute(createTableQuery);
    }

    @Override
    public void add(Article article) {
        jdbcConnector.execute("INSERT INTO article (title, content) VALUES (?, ?)",
                List.of(article.title(), article.content()));
    }

    @Override
    public List<Article> findAll() {
        return jdbcConnector.executeQuery("SELECT id, title, content FROM article",
                resultSet -> {
                    List<Article> articles = new ArrayList<>();
                    try {
                        while (resultSet.next()) {
                            Long id = resultSet.getLong("id");
                            String title = resultSet.getString("title");
                            String content = resultSet.getString("content");
                            articles.add(new Article(id, title, content));
                        }
                    } catch (SQLException e) {
                        throw new JdbcException(e);
                    }
                    return articles;
                });
    }

    @Override
    public Optional<Article> get(long id) {
        Article article = jdbcConnector.executeQuery("SELECT id, title, content FROM article WHERE id = ?",
                List.of(String.valueOf(id)),
                resultSet -> {
                    try {
                        String title = resultSet.getString("title");
                        String content = resultSet.getString("content");
                        return new Article(id, title, content);
                    } catch (SQLException e) {
                        throw new JdbcException(e);
                    }
                });

        return Optional.ofNullable(article);
    }

    @Override
    public void clear() {
        jdbcConnector.execute("TRUNCATE TABLE article");
    }
}
