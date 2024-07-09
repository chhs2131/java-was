package codesquad.http.type;

import codesquad.http.parser.KeyValueParser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cookie {
    private final Map<String, String> cookies = new HashMap<>();

    public String get(String key) {
        return cookies.get(key);
    }

    public boolean contains(String key) {
        return cookies.containsKey(key);
    }

    public void put(String key, String value) {
        cookies.put(key, value);
    }

    public void putAll(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public void putAll(Cookie cookie) {
        this.cookies.putAll(cookie.cookies);
    }

    public static Cookie create(List<String> headerValues) {
        final Cookie cookie = new Cookie();
        for (final String headerValue : headerValues) {
            System.out.println(headerValue);
            cookie.putAll(create(headerValue));
        }
        return cookie;
    }

    public static Cookie create(String headerValue) {
        final Cookie cookie = new Cookie();
        cookie.putAll(KeyValueParser.parseCookie(headerValue));
        return cookie;
    }

    public static Cookie createEmpty() {
        return new Cookie();
    }

    @Override
    public String toString() {
        return "Cookie{" +
            "cookies=" + cookies +
            '}';
    }
}
