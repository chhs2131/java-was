package codesquad.webserver.http;

import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpProtocol;
import codesquad.webserver.http.type.HttpStatus;

public record HttpResponse(
        HttpProtocol protocol,
        HttpStatus status,
        HttpHeader headers,
        String body
) {
}
