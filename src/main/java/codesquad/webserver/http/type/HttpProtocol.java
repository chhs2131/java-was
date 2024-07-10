package codesquad.webserver.http.type;

import java.util.Arrays;

public enum HttpProtocol {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0");

    private final String protocol;

    HttpProtocol(String protocol) {
        this.protocol = protocol;
    }

    public static HttpProtocol from(String protocol) {
        return Arrays.stream(HttpProtocol.values())
                .filter(v -> v.protocol.equals(protocol))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HttpProtocol입니다. protocol: " + protocol));
    }

    @Override
    public String toString() {
        return this.protocol;
    }
}
