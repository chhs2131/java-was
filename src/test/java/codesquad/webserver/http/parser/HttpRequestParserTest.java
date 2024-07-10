package codesquad.webserver.http.parser;

import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.http.type.HttpProtocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpRequestParserTest {
    @Test
    @DisplayName("Body까지 모두 존재하는 경우")
    public void test_correctly_parses_well_formed_http_post_request_with_body() {
        String message = "POST /submit HTTP/1.1\nHost: www.example.com\nContent-Length: 11\n\nHello World";

        HttpRequest request = HttpRequestParser.parse(message);

        assertEquals(HttpMethod.POST, request.method());
        assertEquals("/submit", request.path());
        assertEquals(HttpProtocol.HTTP_1_1, request.protocol());
        assertEquals("www.example.com", request.headers().get("Host"));
        assertEquals("11", request.headers().get("Content-Length"));
        assertEquals("Hello World", request.body().get("raw"));
    }

    @Test
    @DisplayName("QueryString이 존재하는 경우")
    public void test_handles_http_requests_with_malformed_headers_with_query_string() {
        String message = "GET /index.html?param1=value1&param2=value2 HTTP/1.1\nHost www.example.com\n\n";

        HttpRequest request = HttpRequestParser.parse(message);

        assertEquals(HttpMethod.GET, request.method());
        assertEquals("/index.html", request.path());
        assertEquals(HttpProtocol.HTTP_1_1, request.protocol());
        assertTrue(request.headers().isEmpty());
        assertTrue(request.body().get("raw").isEmpty());
        assertTrue(request.queryString().containsKey("param1"));
        assertTrue(request.queryString().containsValue("value1"));
        assertTrue(request.queryString().containsKey("param2"));
        assertTrue(request.queryString().containsValue("value2"));
    }

    @Test
    @DisplayName("Header와 Body가 없는 경우")
    public void test_handles_http_requests_with_no_headers() {
        String message = "GET /index.html HTTP/1.1\n\n";

        HttpRequest request = HttpRequestParser.parse(message);

        assertEquals(HttpMethod.GET, request.method());
        assertEquals("/index.html", request.path());
        assertEquals(HttpProtocol.HTTP_1_1, request.protocol());
        assertTrue(request.headers().isEmpty());
        assertTrue(request.body().get("raw").isEmpty());
    }

    @Test
    @DisplayName("Body가 없는 경우")
    public void test_handles_http_requests_with_no_body() {
        String message = "GET /index.html HTTP/1.1\nHost: www.example.com\n\n";

        HttpRequest request = HttpRequestParser.parse(message);

        assertEquals(HttpMethod.GET, request.method());
        assertEquals("/index.html", request.path());
        assertEquals(HttpProtocol.HTTP_1_1, request.protocol());
        assertEquals("www.example.com", request.headers().get("Host"));
        assertTrue(request.body().get("raw").isEmpty());
    }

    @Test
    @DisplayName("QueryString에 포함된 이메일과 한글 해석을 성공한다")
    public void test_handle_querystring_with_email_and_korean() {
        String message = "GET /user/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: */*";

        HttpRequest request = HttpRequestParser.parse(message);

        assertEquals(HttpMethod.GET, request.method());
        assertEquals("/user/create", request.path());
        assertEquals(HttpProtocol.HTTP_1_1, request.protocol());
        assertEquals("localhost:8080", request.headers().get("Host"));
        assertTrue(request.body().get("raw").isEmpty());

        Map<String, String> query = request.queryString();
        assertEquals("javajigi", query.get("userId"));
        assertEquals("password", query.get("password"));
        assertEquals("박재성", query.get("name"));
        assertEquals("javajigi@slipp.net", query.get("email"));
    }
}
