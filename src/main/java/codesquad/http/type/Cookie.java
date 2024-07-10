package codesquad.http.type;

import codesquad.http.parser.KeyValueParser;
import codesquad.webserver.session.Session;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Cookie {
    private final Map<String, String> cookies = new HashMap<>();

    public String get(String key) {
        return cookies.get(key);
    }

    public Set<String> keySet() {
        return cookies.keySet();
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

    public static Cookie expiredSession() {
        final Cookie cookie = new Cookie();
        cookie.put("SID", "");
        cookie.put("Path", "/");
        cookie.put("Max-Age", "0");
        return cookie;
    }

    public static Cookie from(Session session) {
        final Cookie cookie = new Cookie();
        cookie.put("SID", session.id());
        cookie.put("Path", "/");

        // Max-Age를 세션의 만료 시간을 기준으로 계산
        long maxAgeSeconds = java.time.Duration.between(LocalDateTime.now(), session.expired()).getSeconds();
        cookie.put("Max-Age", String.valueOf(maxAgeSeconds));

        // 만료 시간을 HTTP 쿠키의 Expires 형식으로 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
        String expires = session.expired().format(formatter);
        cookie.put("Expires", expires);

        return cookie;
    }

    @Override
    public String toString() {
        return "Cookie{" +
            "cookies=" + cookies +
            '}';
    }
}
