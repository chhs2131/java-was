package codesquad.application.dao;

import codesquad.application.domain.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleDao {
    void add(Article article);
    List<Article> findAll();
    Optional<Article> get(long id);
    boolean existsById(long id);
    void clear();
}
