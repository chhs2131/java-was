package codesquad.webserver;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import codesquad.webserver.handler.DynamicRequestHandler;
import codesquad.webserver.handler.ReservedRequestHandler;
import codesquad.webserver.handler.RouterHandler;
import codesquad.webserver.handler.StaticFileRequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 요청에 해당하는 로직을 수행하고, HTTP 응답을 진행합니다.
 */
public class ResponseHandler {
    private static final List<RouterHandler> handlers = new ArrayList<>();
    static {
        handlers.add(new DynamicRequestHandler());
        handlers.add(new ReservedRequestHandler());
        handlers.add(new StaticFileRequestHandler());
    }

    public HttpResponse handle(HttpRequest httpRequest) {
        return handlers.stream()
                .filter(handler -> handler.support(httpRequest))
                .findFirst()
                .map(handler -> handler.handle(httpRequest))
                .orElse(createBadRequest());
    }

    private HttpResponse createBadRequest() {
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.BAD_REQUEST, null, "요청이 잘못된 것 같은데요?");
    }
}
