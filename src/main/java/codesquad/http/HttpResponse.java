package codesquad.http;

import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;

import java.util.Map;

public record HttpResponse(
        HttpProtocol protocol,
        HttpStatus status,
        Map<String, String> headers,
        String body
) {
}
