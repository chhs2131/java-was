package codesquad.webserver.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static codesquad.webserver.handler.SimpleTemplateEngine.render;
import static org.assertj.core.api.Assertions.*;

class SimpleTemplateEngineTest {
    private final String template = "<html><head><title>${title}</title></head><body><h1>${header}</h1><p>${body}</p></body></html>";

    @Test
    @DisplayName("모든 값을 전달하면 성공적으로 템플릿엔진이 ${}값을 치환한다.")
    public void test_template_engine_all_success() {
        // 모든 플레이스홀더에 값이 있는 경우
        Map<String, String> value = Map.of(
                "title", "Test Page",
                "header", "Hello, World!",
                "body", "This is a test."
        );

        String result = render(template, value);

        assertThat(result).isEqualTo("<html><head><title>Test Page</title></head><body><h1>Hello, World!</h1><p>This is a test.</p></body></html>");
    }

    @Test
    @DisplayName("일부 값만 존재하면 일부 ${}만 치환된채로 성공적으로 반환한다.")
    public void test_template_engine_two_success() {
        // 일부 플레이스홀더에 값이 없는 경우
        Map<String, String> value = Map.of(
                "title", "Test Page",
                "header", "Hello, World!"
                // "body" key is missing
        );

        String result = render(template, value);

        assertThat(result).isEqualTo("<html><head><title>Test Page</title></head><body><h1>Hello, World!</h1><p>${body}</p></body></html>");
    }

    @Test
    @DisplayName("값을 전달하지 않는 경우 모든 ${}가 그대로 반환한다.")
    public void test_template_engine_zero_success() {
        // 모든 플레이스홀더에 값이 없는 경우
        Map<String, String> value = Map.of();

        String result = render(template, value);

        assertThat(result).isEqualTo("<html><head><title>${title}</title></head><body><h1>${header}</h1><p>${body}</p></body></html>");
    }

    @Test
    @DisplayName("존재하지 않는 값을 전달하는 경우 요청을 무시하고 ${}를 그대로 반환한다.")
    public void test_template_engine_cant_place_success() {
        Map<String, String> value = Map.of("hello", "world");

        String result = render(template, value);

        assertThat(result).isEqualTo("<html><head><title>${title}</title></head><body><h1>${header}</h1><p>${body}</p></body></html>");
    }
}