package codesquad.http.type;

import java.util.Map;

public record StartLine(
        HttpMethod method,
        String path,
        Map<String, String> query,
        HttpProtocol protocol
) {
}
