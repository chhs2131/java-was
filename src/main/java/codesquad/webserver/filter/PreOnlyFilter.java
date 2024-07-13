package codesquad.webserver.filter;

import codesquad.webserver.http.HttpResponse;

public interface PreOnlyFilter extends Filter {
    @Override
    default HttpResponse postFilter(HttpResponse response) {
        return response;
    }
}
