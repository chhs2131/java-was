package codesquad.webserver.handler;

import codesquad.webserver.file.StaticFileReader;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.ContentType;
import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.util.StringUtil;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHttpResponseCreator {
    private static final Logger logger = LoggerFactory.getLogger(FileHttpResponseCreator.class);
    private static final StaticFileReader staticFileReader = new StaticFileReader();

    private FileHttpResponseCreator() {}

    public static HttpResponse create(String resourcePath) {
        logger.debug("정적 파일 응답을 생성합니다. Resource: {}", resourcePath);
        String mimeType = getMimeType(resourcePath);
        HttpHeader headers = HttpHeader.createEmpty();
        headers.add("Content-Type", mimeType);

        String fileData = getStaticFile(resourcePath);
        return HttpResponse.ok(headers, fileData);
    }

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

    private static String getMimeType(String staticFilePath) {
        String extension = StringUtil.getExtension(staticFilePath);
        return ContentType.from(extension).getMimeType();
    }

    /**
     *
     * @param path
     * @return
     * @throws IllegalArgumentException 파일 읽기 실패시 발생
     */
    private static String getStaticFile(String path) {
        return staticFileReader.read(path);
    }
}
