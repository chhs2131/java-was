package codesquad.database;

import codesquad.application.Article;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArticleDatabase {
    private static final List<Article> articles = new CopyOnWriteArrayList<>();

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
