package codesquad.webserver.util.fake;

import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.http.type.HttpMethod;

@Controller
public class FakeController {
    @RequestMapping(method = HttpMethod.GET, path = "/fake")
    public void fake() {
    }
}
