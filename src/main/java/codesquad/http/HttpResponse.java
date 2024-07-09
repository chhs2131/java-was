package codesquad.http;

import codesquad.http.type.HttpHeader;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;

public record HttpResponse(
        HttpProtocol protocol,
        HttpStatus status,
        HttpHeader headers,
        String body
) {
}
