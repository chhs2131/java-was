package codesquad.webserver.handler;

import codesquad.http.type.HttpMethod;

public record HandlerPath(
    HttpMethod method,
    String path
) {
}
