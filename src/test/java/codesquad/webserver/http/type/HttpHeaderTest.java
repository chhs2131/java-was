package codesquad.webserver.http.type;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.webserver.http.type.HttpHeader;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpHeaderTest {
    @ParameterizedTest
    @MethodSource("headerData")
    @DisplayName("하나의 헤더를 정상적으로 생성합니다.")
    public void test_add_single_header_and_retrieve(String name, String value) {
        HttpHeader httpHeader = HttpHeader.createEmpty();

        httpHeader.add("Content-Type", "text/plain");

        assertEquals("text/plain", httpHeader.get("Content-Type"));
    }

    static Stream<Arguments> headerData() {
        return Stream.of(
            Arguments.of("Content-Type", "text/plain"),
            Arguments.of("Content-Length", "1234"),
            Arguments.of("Cookie", "SID=123452hf238h3f2h238h238fh23")
        );
    }

    @Test
    @DisplayName("여러개의 헤더를 정상적으로 생성합니다.")
    public void test_add_multiple_headers_and_retrieve() {
        HttpHeader httpHeader = HttpHeader.createEmpty();

        httpHeader.add("Content-Type", "text/plain");
        httpHeader.add("Content-Length", "1234");

        assertEquals("text/plain", httpHeader.get("Content-Type"));
        assertEquals("1234", httpHeader.get("Content-Length"));
    }

    @Test
    @DisplayName("헤더에 존재하지 않는 값을 요청하면 성공적으로 빈 값을 반환한다.")
    public void test_retrieve_non_existent_header() {
        HttpHeader httpHeader = HttpHeader.createEmpty();

        String result = httpHeader.get("Non-Existent-Header");

        assertEquals("", result);
    }

    @Test
    @DisplayName("동일한 헤더를 정상적으로 생성합니다.")
    public void test_add_multiple_headers() {
        HttpHeader httpHeader = HttpHeader.createEmpty();

        httpHeader.add("Set-Cookie", "type=text/plain");
        httpHeader.add("Set-Cookie", "SID=1234");
        final List<String> all = httpHeader.getAll("Set-Cookie");

        assertTrue(all.contains("SID=1234"));
        assertTrue(all.contains("type=text/plain"));
    }
}
