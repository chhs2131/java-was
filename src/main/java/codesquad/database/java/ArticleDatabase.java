package codesquad.database.java;

import codesquad.application.dao.ArticleDao;
import codesquad.application.domain.Article;
import codesquad.webserver.annotation.Repository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class ArticleDatabase implements ArticleDao {
    private static final List<Article> articles = new CopyOnWriteArrayList<>();

    public void add(Article article) {
        articles.add(article);
    }

    public List<Article> findAll() {
        return Collections.unmodifiableList(articles);
    }

    public Optional<Article> get(long index) {
        return Optional.ofNullable(articles.get((int) index));
    }

    @Override
    public boolean existsById(final long id) {
        return get(id).isPresent();
    }

    public void clear() {
        articles.clear();
    }
}
