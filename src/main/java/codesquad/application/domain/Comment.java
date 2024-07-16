package codesquad.application.domain;

public record Comment(
    Long id,
    Long articleId,
    String name,
    String content
) {
}
