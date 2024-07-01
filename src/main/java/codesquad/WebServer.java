package codesquad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private static final String STATIC_FILE_PATH = "src/main/resources/static";
    private final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private ServerSocket serverSocket;

    public void init(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        logger.debug("Listening for connection on port {} ....", port);
    }

    public void run() throws IOException {
        while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.

            // 1번이 좋을까
            try (Socket clientSocket = serverSocket.accept()) { // 클라이언트 연결을 수락합니다.
                logger.debug("Client connected {} {}", clientSocket.getLocalPort(), clientSocket.getLocalAddress());

                // 2번이 좋을까
                // 사용자 입력을 받는 부분
                String message = getInputStream(clientSocket);

                // HTTP 파싱
                HttpRequest request = parseHttpMessage(message);
                logger.debug("HTTP Request! {} {} {}", request.method(), request.path(), request.protocol());
                logger.debug("HTTP Headers! size: {}", request.headers().size());
                logger.debug("HTTP Body! {}", request.body());

                // 응답을 생성합니다.
                outputStream(clientSocket);
            }
        }
    }

    private HttpRequest parseHttpMessage(String message) {
        return HttpRequestParser.parse(message);
    }

    private String getInputStream(Socket clientSocket) throws IOException {
        InputStream input = clientSocket.getInputStream();
        byte[] inputData = new byte[1024];  // TODO ?? 이건 몇으로 하는게 좋죠?
        int length = input.read(inputData);
        return new String(inputData, 0, length);
    }

    private void outputStream(Socket clientSocket) throws IOException {
        OutputStream clientOutput = clientSocket.getOutputStream();

        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
        clientOutput.write("Content-Type: text/html\r\n".getBytes());
        clientOutput.write("\r\n".getBytes());

        byte[] fileData = getStaticFile("/index.html");
        clientOutput.write(fileData);
        clientOutput.flush();
    }

    private byte[] getStaticFile(String path) throws IOException {
        File file = new File("src/main/resources/static/index.html");
        if (!file.exists()) {
            logger.error("????????");
            // TODO
        }

        FileInputStream fileInputStream = new FileInputStream(STATIC_FILE_PATH + path);
        byte[] fileData = fileInputStream.readAllBytes();
        return fileData;
    }
}
