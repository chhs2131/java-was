package codesquad.webserver.http.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpHeader{
    private final Map<String, List<String>> headers = new HashMap<>();

    public long size() {
        return headers.size();
    }

    public void set(String name, String value) {
        final ArrayList<String> v = new ArrayList<>();
        v.add(value);
        headers.put(name, v);
    }

    public void add(String name, String value) {
        if (existsHeader(name)) {
            set(name, value);
            return;
        }

        headers.get(name).add(value);
    }

    /**
     * Cookie의 값 전체를 Header에 추가합니다.
     * @param cookie
     */
    public void setCookie(final Cookie cookie) {
        final StringBuilder sb = new StringBuilder();

        // TODO Cookie 클래스에서 관련 정보를 받기
        List<String> list = List.of("SID", "Path", "Domain", "Max-Age", "Expires", "Secure", "HttpOnly", "SameSite");
        list.stream()
                .forEach(key -> {
                    if (!cookie.contains(key)) {
                        return;
                    }
                    sb.append(key + "=" + cookie.get(key) + ";");
                });

        headers.put("Set-Cookie", List.of(sb.toString()));
    }

    /**
     * Header에 저장되어있는 값을 모두 반환합니다.
     * @param name (key값)
     * @return List<String>
     */
    public List<String> getAll(String name) {
        final List<String> strings = headers.get(name);
        if (strings == null) {
            return List.of();
        }

        return Collections.unmodifiableList(strings);
    }

    /**
     * Header에 저장되어있는 값 중 첫번째 값을 반환합니다.
     * @param name (key값)
     * @return String (값이 존재하지 않는 경우 빈 리터럴 ""을 반환합니다.
     */
    public String get(String name) {
        final List<String> all = getAll(name);
        if (all.isEmpty()) {
            return "";
        }
        return getAll(name).get(0);
    }

    public boolean contains(String name) {
        return headers.containsKey(name);
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    public Set<Entry<String, List<String>>> entrySet() {
        return headers.entrySet();
    }

    public Cookie getCookies() {
        if (!contains("Cookie")) {
            return Cookie.createEmpty();
        }

        // Cookie 벨류 리스트를 Cookie 클래스에게 전달해 파싱하고 그것을 반환한다.
        return Cookie.create(headers.get("Cookie"));
    }

    public HttpHeader setLocation(String location) {
        add("Location", location);
        return this;
    }

    public static HttpHeader createRedirection(String location) {
        final HttpHeader httpHeader = new HttpHeader();
        httpHeader.setLocation(location);
        return httpHeader;
    }

    public static HttpHeader of(String name, String value) {
        final HttpHeader httpHeader = new HttpHeader();
        httpHeader.add(name, value);
        return httpHeader;
    }

    public static HttpHeader of(String name, String value, String name2, String value2) {
        final HttpHeader httpHeader = new HttpHeader();
        httpHeader.add(name, value);
        httpHeader.add(name2, value2);
        return httpHeader;
    }

    public static HttpHeader createEmpty() {
        return new HttpHeader();
    }

    private boolean existsHeader(String name) {
        return !headers.containsKey(name) || headers.get(name) == null || headers.get(name).isEmpty();
    }
}
