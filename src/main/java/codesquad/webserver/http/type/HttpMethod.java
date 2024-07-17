package codesquad.webserver.http.type;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    PATCH,
    TRACE;

    public boolean isGet() {
        return this == GET;
    }

    public static HttpMethod from(String name) {
        return Arrays.stream(HttpMethod.values())
                .filter(v -> v.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HttpMethod입니다. name: " + name));
    }

    @Override
    public String toString() {
        return this.name();
    }
}
