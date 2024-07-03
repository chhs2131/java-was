package codesquad.http;

import java.util.Map;

public record HttpRequest(
        String method,
        String path,
        Map<String, String> queryString,
        String protocol,
        Map<String, String> headers,
        String body
) {
}
