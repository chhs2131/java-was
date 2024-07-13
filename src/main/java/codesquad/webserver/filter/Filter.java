package codesquad.webserver.filter;

import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;

public interface Filter {
    int getOrder();
    HttpRequest preFilter(HttpRequest request);
    HttpResponse postFilter(HttpResponse response);
}
