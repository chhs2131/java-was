package codesquad.webserver.http.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class BodyParserTest {
    @Test
    @DisplayName("Body값을 정상적으로 파싱한다.")
    public void parse() {
        String[] lines = {
                "POST / HTTP/1.1",
                "Host: example.com",
                "Content-Length: 11",
                "",
                "hello world"
        };
        int contentLength = 11;
        String expected = "hello world";

        String result = BodyParser.parse(lines, contentLength);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("여러 Form값이 전달되어도 정상 파싱한다.")
    public void testParseFormBody_multiplePairs() {
        String[] lines = {
                "POST / HTTP/1.1",
                "Host: example.com",
                "Content-Length: 35",
                "",
                "key1=value1&key2=value2&key3=value3"
        };
        int contentLength = 35;

        Map<String, String> result = BodyParser.parseFormBody(lines, contentLength);
        assertThat(result.get("key1")).isEqualTo("value1");
        assertThat(result.get("key2")).isEqualTo("value2");
        assertThat(result.get("key3")).isEqualTo("value3");
    }
}