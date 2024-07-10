package codesquad.webserver.handler;

import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;

public interface RouterHandler {
    boolean support(HttpRequest httpRequest);
    HttpResponse handle(HttpRequest httpRequest);
}
