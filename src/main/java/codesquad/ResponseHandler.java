package codesquad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Socket을 통해 HTTP 응답을 진행합니다.
 */
public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private static final String STATIC_FILE_PATH = "src/main/resources/static";

    public void handle(Socket clientSocket) {
        outputStream(clientSocket);
    }

    private void outputStream(Socket clientSocket) {
        try {
            OutputStream clientOutput = clientSocket.getOutputStream();

            clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
            clientOutput.write("Content-Type: text/html\r\n".getBytes());
            clientOutput.write("\r\n".getBytes());

            byte[] fileData = getStaticFile("/index.html");
            clientOutput.write(fileData);
            clientOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("응답 반환중 문제가 발생했어요. ^_^");
        }
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
