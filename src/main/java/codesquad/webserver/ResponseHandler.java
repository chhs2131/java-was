package codesquad.webserver;

import codesquad.http.type.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import codesquad.model.User;
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
        String path = httpRequest.path();

        if (path.equals("/create")) {
            String name = httpRequest.queryString().get("name");
            String password = httpRequest.queryString().get("password");
            String nickname = httpRequest.queryString().get("nickname");

            final User user = new User(name, password, nickname);
            logger.debug("회원가입을 완료했습니다. {}", user);

            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.CREATED, null, "유저가 생성되었습니다.");
        }

        // TODO 정적 파일에 대한 요청인지 판단을 어떻게 할 것인가요??
        String mimeType = "";
        String fileData = "";
        String resourcePath = "";
        Map<String, String> headers = new HashMap<>();

        if (path.equals("/registration")) {
            mimeType = ContentType.HTML.getMimeType();
            headers.put("Content-Type", mimeType);
            resourcePath = "/registration/index.html";
        }
        else if (isStaticRequest(path)) {
            mimeType = getContentType(path);
            headers.put("Content-Type", mimeType);
            resourcePath = path;
        }

        // 정적 파일이 아닌 경우
        if (resourcePath.isEmpty()) {
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.BAD_REQUEST, null, "요청이 잘못된 것 같은데요?");
        }

        // 정적 파일 응답
        try {
            fileData = getStaticFile(resourcePath);
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.OK, headers, fileData);
        } catch (Exception e) {
            logger.debug("HTTP NotFound Exception. {}", resourcePath);
            return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.NOT_FOUND, null, "못찾겠습니다~");
        }
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
