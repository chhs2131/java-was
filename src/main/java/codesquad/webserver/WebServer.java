package codesquad.webserver;

import codesquad.webserver.util.AnnotationScanner;
import codesquad.webserver.handler.DynamicRequestHandler;
import codesquad.webserver.handler.RouterHandler;
import codesquad.webserver.handler.StaticRequestHandler;
import codesquad.webserver.util.ClassFinder;
import java.util.ArrayList;
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

    public void init(int port, String basePackage) throws IOException {
        serverSocket = new ServerSocket(port);
        logger.debug("Listening for connection on port {} ....", port);
        requestHandler = new RequestHandler(getHandlerList(basePackage));
        httpThreadPool = new HttpThreadPool(Executors.newFixedThreadPool(THREAD_POOL_SIZE));
    }

    public void run() throws IOException {
        while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.
            final Socket clientSocket = serverSocket.accept();
            logger.debug("Client connected {} {}", clientSocket.getLocalPort(), clientSocket.getLocalAddress());
            httpThreadPool.execute(new HttpTask(clientSocket, requestHandler));
        }
    }

    public List<RouterHandler> getHandlerList(String basePackage) {
      // 클래스 스캔
       try {
           final List<Class<?>> classes = ClassFinder.getClassesForPackage(basePackage);
           final List<Class<?>> components = AnnotationScanner.getComponents(classes);

           List<RouterHandler> handlers = new ArrayList<>();
           handlers.add(new DynamicRequestHandler(
                   AnnotationScanner.getRequestMap(components),
                   AnnotationScanner.getInstances(components)));
           handlers.add(new StaticRequestHandler());
           return handlers;
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
}
