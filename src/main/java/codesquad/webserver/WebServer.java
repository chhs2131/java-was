package codesquad.webserver;

import codesquad.webserver.handler.*;
import codesquad.webserver.util.AnnotationScanner;
import java.util.List;
import java.util.Map;
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
        long startTime = System.currentTimeMillis(); // 측정 시작 시간

        logger.debug("Listening for connection on port {} ....", port);
        serverSocket = new ServerSocket(port);
        requestHandler = new RequestHandler(List.of(
            getDynamicHandler(basePackage),
            new StorageFileHandler(),
            new StaticRequestHandler()
        ));
        httpThreadPool = new HttpThreadPool(Executors.newFixedThreadPool(THREAD_POOL_SIZE));

        long endTime = System.currentTimeMillis(); // 측정 종료 시간
        long elapsedTime = endTime - startTime; // 경과 시간 계산
        logger.debug("서버 동작 준비 완료: {}ms", elapsedTime); // 실행 시간 로깅 혹은 출력
    }

    public void run() throws IOException {
        while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.
            final Socket clientSocket = serverSocket.accept();
            logger.debug("Client connected {} {}", clientSocket.getLocalPort(), clientSocket.getLocalAddress());
            httpThreadPool.execute(new HttpTask(clientSocket, requestHandler));
        }
    }

    public RouterHandler getDynamicHandler(String basePackage) throws IOException, ClassNotFoundException {
        AnnotationScanner annotationScanner = new AnnotationScanner();
        annotationScanner.init(basePackage);
        Map<HandlerPath, ExecutableHandler> requestMapping = annotationScanner.getRequestMapping();
        return new DynamicRequestHandler(requestMapping);
    }
}
