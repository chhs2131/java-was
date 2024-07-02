package codesquad.webserver;

import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Socket을 통해 HTTP 응답을 진행합니다.
 */
public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private static final String STATIC_FILE_PATH = "src/main/resources/static";

    public HttpResponse handle(HttpRequest httpRequest) {
        // TODO 정적파일만 고려해서 반환중!
        String resourcePath = httpRequest.path();

        String mimeType = getContentType(resourcePath);
        String fileData = getStaticFile(resourcePath);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", mimeType);
        return new HttpResponse("HTTP/1.1", "200 OK", headers, fileData);

    }

    private String getContentType(String staticFilePath) {
        String extension = StringUtil.getExtension(staticFilePath);
        return ContentType.from(extension).getMimeType();
    }

    private String getStaticFile(String path) {
        String filePath = STATIC_FILE_PATH + path;

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다. path: " + filePath);
        }

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            byte[] fileBytes = fileInputStream.readAllBytes();
            return new String(fileBytes, "UTF-8");
        }  catch (FileNotFoundException e) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다. path: " + filePath, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽는 중 오류가 발생했습니다. path: " + filePath, e);
        }
    }
}
