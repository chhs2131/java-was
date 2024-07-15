package codesquad;

import codesquad.webserver.WebServer;
import java.io.IOException;


public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        WebServer webServer = new WebServer();
        webServer.init(PORT, "codesquad.application");
        webServer.run();
    }
}
