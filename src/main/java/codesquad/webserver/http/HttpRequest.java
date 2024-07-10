package codesquad.webserver.http;

import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.http.type.HttpProtocol;

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
