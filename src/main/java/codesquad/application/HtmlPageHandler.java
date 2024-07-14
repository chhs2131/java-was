package codesquad.application;

import static codesquad.webserver.file.FileHttpResponseCreator.create;

import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpMethod;

@Controller
public class HtmlPageHandler {
    @RequestMapping(method = HttpMethod.GET, path = "/registration")
    public HttpResponse getRegistrationPage(HttpRequest httpRequest) {
        return create("/registration/index.html");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public HttpResponse getLoginPage(HttpRequest httpRequest) {
        return create("/login/index.html");
    }
}
