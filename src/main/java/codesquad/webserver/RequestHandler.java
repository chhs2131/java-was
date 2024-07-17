package codesquad.webserver;

import codesquad.webserver.file.ErrorPageResponseFactory;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.handler.*;

import java.util.List;

/**
 * 요청에 해당하는 handler를 찾아 처리하고 HTTP 응답을 반환합니다.
 */
public class RequestHandler {
    private final List<RouterHandler> handlers;

    public RequestHandler(final List<RouterHandler> handlers) {
        this.handlers = handlers;
    }

    public HttpResponse handle(HttpRequest httpRequest) {
        return handlers.stream()
                .filter(handler -> handler.support(httpRequest))
                .findFirst()
                .map(handler -> handler.handle(httpRequest))
                .orElse(createBadRequest());
    }

    private HttpResponse createBadRequest() {
        // TODO 이게 왜 자꾸 실행도지?
        return ErrorPageResponseFactory.badRequest();
    }
}
