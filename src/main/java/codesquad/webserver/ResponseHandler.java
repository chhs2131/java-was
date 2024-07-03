package codesquad.webserver;

import codesquad.http.type.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
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
        String resourcePath = httpRequest.path();

        if (isStaticRequest(resourcePath)) {
            String mimeType = getContentType(resourcePath);
            String fileData = getStaticFile(resourcePath);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", mimeType);
            try {
                return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.OK, headers, fileData);
            } catch (Exception e) {
                return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.NOT_FOUND, null, "못찾겠습니다~");
            }
        }

        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.BAD_REQUEST, null, "요청이 잘못된 것 같은데요?");
    }

    private boolean isStaticRequest(String resourcePath) {
        String extension = StringUtil.getExtension(resourcePath);
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return true;
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
