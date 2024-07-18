package codesquad.database.java;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.application.domain.Article;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArticleDatabaseTest {
    private ArticleDatabase database;

    @BeforeEach
    public void setUp() {
        database = new ArticleDatabase();
    }

    @Test
    @DisplayName("게시글을 정상적으로 추가합니다.")
    public void add_article_to_database() {
        Article article = new Article(1L, "Title", "Content", "ImagePath");

        database.add(article);

        assertTrue(database.existsById(1L));
    }

    @Test
    @DisplayName("여러 게시글도 정상적으로 추가됩니다.")
    public void retrieve_all_articles_from_database() {
        Article article1 = new Article(1L, "Title1", "Content1", "ImagePath1");
        Article article2 = new Article(2L, "Title2", "Content2", "ImagePath2");
        database.add(article1);
        database.add(article2);

        List<Article> articles = database.findAll();

        assertEquals(2, articles.size());
        assertTrue(articles.contains(article1));
        assertTrue(articles.contains(article2));
    }

    @Test
    @DisplayName("null을 추가하려는 경우 예외가 발생합니다.")
    public void add_null_article_to_database() {
        assertThrows(IllegalArgumentException.class, () -> {
            database.add(null);
        });
    }
}