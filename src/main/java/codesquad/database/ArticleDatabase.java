package codesquad.database;

import codesquad.application.domain.Article;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArticleDatabase {
    private static final List<Article> articles = new CopyOnWriteArrayList<>();

    private ArticleDatabase() {}

    public static void add(Article article) {
        articles.add(article);
    }

    public static List<Article> getAll() {
        return Collections.unmodifiableList(articles);
    }

    public static Optional<Article> get(int index) {
        return Optional.ofNullable(articles.get(index));
    }

    public static void clear() {
        articles.clear();
    }
}
