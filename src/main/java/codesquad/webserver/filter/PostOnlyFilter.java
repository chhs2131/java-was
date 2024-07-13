package codesquad.webserver.filter;

import codesquad.webserver.http.HttpRequest;

public interface PostOnlyFilter extends Filter {
    @Override
    default HttpRequest preFilter(HttpRequest httpRequest) {
        return httpRequest;
    }
}
