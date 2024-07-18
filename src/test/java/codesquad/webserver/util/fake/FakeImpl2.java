package codesquad.webserver.util.fake;

import codesquad.webserver.annotation.Controller;

@Controller
public class FakeImpl2 implements FakeInterface {
    @Override
    public int getNumber() {
        return 2;
    }
}
