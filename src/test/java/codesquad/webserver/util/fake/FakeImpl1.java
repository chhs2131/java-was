package codesquad.webserver.util.fake;

import codesquad.webserver.annotation.Controller;

@Controller
public class FakeImpl1 implements FakeInterface {
    @Override
    public int getNumber() {
        return 1;
    }
}
