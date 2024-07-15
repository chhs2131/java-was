package codesquad.webserver;

import codesquad.webserver.util.AnnotationScanner;
import codesquad.webserver.handler.DynamicRequestHandler;
import codesquad.webserver.handler.RouterHandler;
import codesquad.webserver.handler.StaticRequestHandler;
import codesquad.webserver.util.ClassFinder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int THREAD_POOL_SIZE = 10;
    private RequestHandler requestHandler;
    private ServerSocket serverSocket;
    private HttpThreadPool httpThreadPool;

    public void init(int port, String basePackage) throws IOException, ClassNotFoundException {
        logger.debug("Listening for connection on port {} ....", port);
        serverSocket = new ServerSocket(port);
        RouterHandler dynamicHandler = getDynamicHandler(basePackage);
        requestHandler = new RequestHandler(List.of(dynamicHandler, new StaticRequestHandler()));
        httpThreadPool = new HttpThreadPool(Executors.newFixedThreadPool(THREAD_POOL_SIZE));
    }

    public void run() throws IOException {
        while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.
            final Socket clientSocket = serverSocket.accept();
            logger.debug("Client connected {} {}", clientSocket.getLocalPort(), clientSocket.getLocalAddress());
            httpThreadPool.execute(new HttpTask(clientSocket, requestHandler));
        }
    }

    public RouterHandler getDynamicHandler(String basePackage) throws IOException, ClassNotFoundException {
        final List<Class<?>> classes = ClassFinder.getClassesForPackage(basePackage);
        final List<Class<?>> components = AnnotationScanner.getComponents(classes);
        return new DynamicRequestHandler(
            AnnotationScanner.getRequestMap(components),
            AnnotationScanner.getInstances(components));
    }
}
