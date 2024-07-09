package codesquad.http;

import codesquad.http.type.HttpHeader;
import codesquad.http.type.HttpMethod;
import codesquad.http.type.HttpProtocol;

import java.util.Map;

public record HttpRequest(
        HttpMethod method,
        String path,
        Map<String, String> queryString,
        HttpProtocol protocol,
        HttpHeader headers,
        Map<String, String> body
) {
}
