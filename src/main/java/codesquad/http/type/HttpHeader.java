package codesquad.http.type;

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
        if (!headers.containsKey(name) || headers.get(name) == null || headers.get(name).isEmpty()) {
            set(name, value);
            return;
        }

        headers.get(name).add(value);
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
}
