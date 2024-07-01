package codesquad;

import java.io.IOException;


public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        WebServer webServer = new WebServer();
        webServer.init(PORT);
        webServer.run();
    }
}
