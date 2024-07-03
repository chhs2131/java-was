package codesquad.http;

import codesquad.http.type.HttpMethod;
import codesquad.http.type.HttpProtocol;

import java.util.Map;

public record HttpRequest(
        HttpMethod method,
        String path,
        Map<String, String> queryString,
        HttpProtocol protocol,
        Map<String, String> headers,
        String body
) {
}
