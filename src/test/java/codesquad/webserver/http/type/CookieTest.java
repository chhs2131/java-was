package codesquad.webserver.http.type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {
    @Test
    @DisplayName("Key-Value를 통해 쿠키 생성시 관련 값을 기반으로 정상 생성된다.")
    public void test_create_cookie_from_single_header_value() {
        String headerValue = "sessionId=abc123";

        Cookie cookie = Cookie.create(headerValue);

        assertEquals("abc123", cookie.get("sessionId"));
    }

    @Test
    @DisplayName("여러개의 Key-Value값을 통해 쿠키 생성시 관련 값을 기반으로 정상 생성된다.")
    public void test_create_cookie_from_multiple_header_values() {
        List<String> headerValues = Arrays.asList("sessionId=abc123", "userId=789xyz");

        Cookie cookie = Cookie.create(headerValues);

        assertEquals("abc123", cookie.get("sessionId"));
        assertEquals("789xyz", cookie.get("userId"));
    }

    @Test
    @DisplayName("expiredSession로 만료된 세션을 나타내는 쿠키를 정상 생성한다.")
    public void adds_empty_sid_to_cookie() {
        Cookie cookie = Cookie.expiredSession();

        String sid = cookie.get("SID");
        String maxAge = cookie.get("Max-Age");

        assertEquals("", sid);
        assertEquals("0", maxAge);
    }

    @Test
    @DisplayName("비어있는 쿠키에서 모든 값은 null을 반환한다.")
    public void create_empty_cookie() {
        Cookie cookie = Cookie.createEmpty();

        final String sid = cookie.get("SID");

        assertNull(sid);
    }
}
