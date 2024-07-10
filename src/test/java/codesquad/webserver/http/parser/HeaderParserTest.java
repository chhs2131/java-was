package codesquad.webserver.http.parser;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.webserver.http.parser.HeaderParser;
import codesquad.webserver.http.type.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HeaderParserTest {
    @Test
    @DisplayName("쿠키 헤더를 성공적으로 파싱합니다.")
    public void test_parses_cookie_header_correctly() {
        String[] lines = {
            "GET / HTTP/1.1",
            "Host: example.com",
            "Connection: keep-alive",
            "Cookie: sessionId=abc123; userId=789xyz"
        };

        HttpHeader result = HeaderParser.parse(lines);

        System.out.println(result.getCookies());

        assertEquals(3, result.size());
        assertEquals("example.com", result.get("Host"));
        assertEquals("keep-alive", result.get("Connection"));
        assertEquals("sessionId=abc123; userId=789xyz", result.get("Cookie"));
    }

    @Test
    @DisplayName("일반 요청을 성공적으로 파싱합니다.")
    public void test_handles_multiple_headers_with_different_names_and_values() {
        String[] lines = {
            "GET / HTTP/1.1",
            "Host: example.com",
            "Connection: keep-alive",
            "User-Agent: Mozilla/5.0"
        };

        HttpHeader result = HeaderParser.parse(lines);

        assertEquals(3, result.size());
        assertEquals("example.com", result.get("Host"));
        assertEquals("keep-alive", result.get("Connection"));
        assertEquals("Mozilla/5.0", result.get("User-Agent"));
    }

    @Test
    @DisplayName("요청이 비어있는 경우 성공적으로 빈 값을 반환합니다.")
    public void test_handles_empty_header_string_array() {
        String[] lines = {};

        HttpHeader result = HeaderParser.parse(lines);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("헤더가 존재하지 않는 경우 성공적으로 빈 값을 반환합니다.")
    public void test_handles_header_string_array_with_one_element() {
        String[] lines = {"GET / HTTP/1.1"};

        HttpHeader result = HeaderParser.parse(lines);

        assertTrue(result.isEmpty());
    }
}