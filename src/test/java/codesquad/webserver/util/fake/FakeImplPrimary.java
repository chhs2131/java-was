package codesquad.webserver.util.fake;

import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.Primary;

@Controller
@Primary
public class FakeImplPrimary implements FakeInterface {
    @Override
    public int getNumber() {
        return 100;
    }
}
