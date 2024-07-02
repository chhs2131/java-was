package codesquad;

import codesquad.webserver.HttpTask;
import codesquad.webserver.HttpThreadPool;
import codesquad.webserver.RequestHandler;
import codesquad.webserver.ResponseHandler;
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
    private ResponseHandler responseHandler;
    private ServerSocket serverSocket;
    private HttpThreadPool httpThreadPool;

    public void init(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        logger.debug("Listening for connection on port {} ....", port);
        requestHandler = new RequestHandler();
        responseHandler = new ResponseHandler();
        httpThreadPool = new HttpThreadPool(Executors.newFixedThreadPool(THREAD_POOL_SIZE));
    }

    public void run() throws IOException {
        while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.
            final Socket clientSocket = serverSocket.accept();
            logger.debug("Client connected {} {}", clientSocket.getLocalPort(), clientSocket.getLocalAddress());
            httpThreadPool.execute(new HttpTask(clientSocket, requestHandler, responseHandler));
        }
    }
}
