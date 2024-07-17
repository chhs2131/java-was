package codesquad.application.domain;

public record Article(
        Long id,
        String title,
        String content,
        String imagePath
) {
}
