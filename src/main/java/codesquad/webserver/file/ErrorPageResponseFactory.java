package codesquad.webserver.file;

import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpStatus;
import java.util.Map;

public class ErrorPageResponseFactory {
    private static final String BAD_REQUEST_PAGE = "/error/badrequest.html";
    private static final String INTERNAL_SERVER_ERROR_PAGE = "/error/internal.html";
    private static final String NOT_FOUND_PAGE = "/error/notfound.html";

    public static HttpResponse badRequest() {
        return FileHttpResponseCreator.create(HttpStatus.BAD_REQUEST, BAD_REQUEST_PAGE);
    }

    public static HttpResponse internalServerError(Map<String, String> templateMap) {
        return FileHttpResponseCreator.create(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_PAGE, templateMap);
    }

    public static HttpResponse notFound() {
        return FileHttpResponseCreator.create(HttpStatus.NOT_FOUND, NOT_FOUND_PAGE);
    }
}
