package codesquad.http;

import java.util.Map;

public record HttpResponse(
        String protocol,
        String status,
        Map<String, String> headers,
        String body
) {
}
