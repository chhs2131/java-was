package codesquad.webserver;

import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
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

    public void handle(Socket clientSocket, HttpRequest httpRequest) {
        // TODO 정적파일만 고려해서 반환중!
        String resourcePath = httpRequest.path();
        outputStream(clientSocket, resourcePath);
    }

    private void outputStream(Socket clientSocket, String staticFilePath) {
        try {
            OutputStream clientOutput = clientSocket.getOutputStream();

            String mimeType = getContentType(staticFilePath);

            clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
            clientOutput.write(("Content-Type: " + mimeType + "\r\n").getBytes());
            clientOutput.write("\r\n".getBytes());

            byte[] fileData = getStaticFile(staticFilePath);
            clientOutput.write(fileData);
            clientOutput.flush();

            logger.debug("정상 응답 완료");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("응답 반환중 문제가 발생했어요. ^_^");
        }
    }

    private String getContentType(String staticFilePath) {
        String[] split = staticFilePath.split("\\.");
        if (split.length == 0) {
            return "";
        }

        String extension = split[split.length - 1];
        return ContentType.from(extension).getMimeType();
    }

    private byte[] getStaticFile(String path) throws IOException {
        String filePath = STATIC_FILE_PATH + path;

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다. path: " + filePath);
        }

        FileInputStream fileInputStream = new FileInputStream(filePath);
        return fileInputStream.readAllBytes();
    }
}
