package codesquad.webserver.file;

import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.*;
import codesquad.webserver.util.StringUtil;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHttpResponseCreator {
    private static final Logger logger = LoggerFactory.getLogger(FileHttpResponseCreator.class);
    private static final StaticFileReader staticFileReader = new StaticFileReader();

    private FileHttpResponseCreator() {}

    /**
     * 전달된 경로를 기반으로 정적 파일 응답을 생성합니다.
     * @param resourcePath 읽어들일 파일 경로
     * @return static 패키지 기준으로 불러들인 파일
     */
    public static HttpResponse create(String resourcePath) {
        logger.debug("정적 파일 응답을 생성합니다. Resource: {}", resourcePath);
        String mimeType = getMimeType(resourcePath);
        HttpHeader headers = HttpHeader.createEmpty();
        headers.add("Content-Type", mimeType);

        String fileData = getStaticFile(resourcePath);
        return HttpResponse.ok(headers, fileData);
    }

    /**
     * 전달된 경로를 기반으로 정적 파일 응답을 생성합니다.
     * @param status 응답할 StatusCode 정보
     * @param resourcePath 읽어들일 파일 경로
     * @return static 패키지 기준으로 불러들인 파일
     */
    public static HttpResponse create(HttpStatus status, String resourcePath) {
        logger.debug("정적 파일 응답을 생성합니다. Resource: {}", resourcePath);
        String mimeType = getMimeType(resourcePath);
        HttpHeader headers = HttpHeader.createEmpty();
        headers.add("Content-Type", mimeType);

        String fileData = getStaticFile(resourcePath);

        return new HttpResponse(HttpProtocol.HTTP_1_1, status, headers, fileData);
    }

    /**
     * 전달된 경로를 기반으로 파일을 읽고 템플릿 엔진을 통해 동적 응답을 생성합니다.
     * @param resourcePath 읽어들일 파일 경로
     * @param templateData 템플릿 엔진을 통해 치환할 데이터
     * @return static 패키지 기준으로 불러들인후 템플릿 엔진을 통해 생성된 파일
     */
    public static HttpResponse create(String resourcePath, Map<String, String> templateData) {
        logger.debug("동적 파일 응답을 생성합니다. Resource: {}, MappingData: {}", resourcePath, templateData);
        String mimeType = getMimeType(resourcePath);
        HttpHeader headers = HttpHeader.createEmpty();
        headers.add("Content-Type", mimeType);

        // TODO TemplateEngine을 인터페이스로 두고 사용자가 이를 선택할 수 있도록 수정
        String fileData = getStaticFile(resourcePath);
        String templateHtml = SimpleTemplateEngine.render(fileData, templateData);
        return HttpResponse.ok(headers, templateHtml);
    }

    /**
     * 전달된 경로를 기반으로 파일을 읽고 템플릿 엔진을 통해 동적 응답을 생성합니다.
     * @param status 응답할 StatusCode 정보
     * @param resourcePath 읽어들일 파일 경로
     * @param templateData 템플릿 엔진을 통해 치환할 데이터
     * @return static 패키지 기준으로 불러들인후 템플릿 엔진을 통해 생성된 파일
     */
    public static HttpResponse create(HttpStatus status, String resourcePath, Map<String, String> templateData) {
        logger.debug("동적 파일 응답을 생성합니다. Resource: {}, MappingData: {}", resourcePath, templateData);
        String mimeType = getMimeType(resourcePath);
        HttpHeader headers = HttpHeader.createEmpty();
        headers.add("Content-Type", mimeType);

        // TODO TemplateEngine을 인터페이스로 두고 사용자가 이를 선택할 수 있도록 수정
        String fileData = getStaticFile(resourcePath);
        String templateHtml = SimpleTemplateEngine.render(fileData, templateData);

        return new HttpResponse(HttpProtocol.HTTP_1_1, status, headers, templateHtml);
    }

    private static String getMimeType(String staticFilePath) {
        String extension = StringUtil.getExtension(staticFilePath);
        return ContentType.from(extension).getMimeType();
    }

    // @throws IllegalArgumentException 파일 읽기 실패시 발생
    private static String getStaticFile(String path) {
        return staticFileReader.read(path);
    }
}
