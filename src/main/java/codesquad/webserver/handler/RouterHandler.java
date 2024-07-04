package codesquad.webserver.handler;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

public interface RouterHandler {
    boolean support(HttpRequest httpRequest);
    HttpResponse handle(HttpRequest httpRequest);
}
