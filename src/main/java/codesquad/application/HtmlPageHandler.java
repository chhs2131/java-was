package codesquad.application;

import static codesquad.webserver.file.FileHttpResponseCreator.create;

import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;

public class HtmlPageHandler {
    private HtmlPageHandler() {}

    public static HttpResponse getRegistrationPage(HttpRequest httpRequest) {
        return create("/registration/index.html");
    }

    public static HttpResponse getLoginPage(HttpRequest httpRequest) {
        return create("/login/index.html");
    }
}
