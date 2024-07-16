package codesquad.application.dao;

import codesquad.application.domain.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentDao {
    void add(Comment comment);
    Optional<Comment> findById(long id);
    List<Comment> findAllByArticleId(long articleId);
}
