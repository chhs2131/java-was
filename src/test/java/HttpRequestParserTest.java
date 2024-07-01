import codesquad.HttpRequest;
import codesquad.HttpRequestParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpRequestParserTest {
    @Test
    @DisplayName("Body까지 모두 존재하는 경우")
    public void test_correctly_parses_well_formed_http_post_request_with_body() {
        // Given
        String message = "POST /submit HTTP/1.1\nHost: www.example.com\nContent-Length: 11\n\nHello World";

        // When
        HttpRequest request = HttpRequestParser.parse(message);

        // Then
        assertEquals("POST", request.method());
        assertEquals("/submit", request.path());
        assertEquals("HTTP/1.1", request.protocol());
        assertEquals("www.example.com", request.headers().get("Host"));
        assertEquals("11", request.headers().get("Content-Length"));
        assertEquals("Hello World", request.body());
    }

    @Test
    @DisplayName("Header와 Body가 없는 경우")
    public void test_handles_http_requests_with_no_headers() {
        // Given
        String message = "GET /index.html HTTP/1.1\n\n";

        // When
        HttpRequest request = HttpRequestParser.parse(message);

        // Then
        assertEquals("GET", request.method());
        assertEquals("/index.html", request.path());
        assertEquals("HTTP/1.1", request.protocol());
        assertTrue(request.headers().isEmpty());
        assertTrue(request.body().isEmpty());
    }

    @Test
    @DisplayName("Body가 없는 경우")
    public void test_handles_http_requests_with_no_body() {
        // Given
        String message = "GET /index.html HTTP/1.1\nHost: www.example.com\n\n";

        // When
        HttpRequest request = HttpRequestParser.parse(message);

        // Then
        assertEquals("GET", request.method());
        assertEquals("/index.html", request.path());
        assertEquals("HTTP/1.1", request.protocol());
        assertEquals("www.example.com", request.headers().get("Host"));
        assertTrue(request.body().isEmpty());
    }
}
