package codesquad.webserver.util.fake;

public class FakeConstructor {
    public FakeInterface fakeInterface;

    public FakeConstructor(FakeInterface fakeInterface) {
        this.fakeInterface = fakeInterface;
    }

    public int getNumber() {
        return fakeInterface.getNumber();
    }
}
