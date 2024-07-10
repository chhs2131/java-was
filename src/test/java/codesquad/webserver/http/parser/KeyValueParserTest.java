package codesquad.webserver.http.parser;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.webserver.http.type.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KeyValueParserTest {
    @Test
    @DisplayName("쿠키 헤더를 성공적으로 파싱합니다.")
    public void test_parse_single_cookie() {
        String cookieHeader = "sessionId=abc123";

        Cookie cookie = Cookie.create(cookieHeader);

        assertEquals("abc123", cookie.get("sessionId"));
    }

    @Test
    @DisplayName("여러 개의 쿠키를 성공적으로 파싱합니다.")
    public void test_parse_multiple_cookies() {
        String cookieHeader = "sessionId=abc123; userId=789xyz";

        Cookie cookie = Cookie.create(cookieHeader);

        assertEquals("abc123", cookie.get("sessionId"));
        assertEquals("789xyz", cookie.get("userId"));
    }

    @Test
    @DisplayName("비어있는 쿠키 헤더를 성공적으로 처리합니다.")
    public void test_parse_empty_cookie_header() {
        String cookieHeader = "";

        Cookie cookie = Cookie.create(cookieHeader);

        assertTrue(cookie.toString().contains("cookies={}"));
    }

    @Test
    @DisplayName("잘못된 쿠키 형식을 처리합니다.")
    public void test_parse_invalid_cookie_format() {
        String cookieHeader = "invalidcookie";

        Cookie cookie = Cookie.create(cookieHeader);

        assertTrue(cookie.toString().contains("cookies={}"));
    }

    @Test
    @DisplayName("값이 없는 쿠키를 성공적으로 처리합니다.")
    public void test_parse_cookie_with_empty_value() {
        String cookieHeader = "sessionId=";

        Cookie cookie = Cookie.create(cookieHeader);

        assertEquals("", cookie.get("sessionId"));
    }
}