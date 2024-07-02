package codesquad.http;

import java.util.Map;

public record HttpRequest(
        String method,
        String path,
        String protocol,
        Map<String, String> headers,
        String body
) {
}
