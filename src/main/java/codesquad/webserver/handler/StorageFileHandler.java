package codesquad.webserver.handler;

import codesquad.webserver.file.FileHttpResponseCreator;
import codesquad.webserver.file.StorageFileManager;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.ContentType;
import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.util.StringUtil;
import java.util.Arrays;

public class StorageFileHandler implements RouterHandler {
    @Override
    public boolean support(final HttpRequest httpRequest) {
        final String path = httpRequest.path();
        final HttpMethod method = httpRequest.method();
        return method.isGet() && path.startsWith("/uploads");
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String path = httpRequest.path();
        final byte[] bytes = StorageFileManager.readFile(path);

        String extension = StringUtil.getExtension(path);
        final String mimeType = ContentType.from(extension).getMimeType();
        return HttpResponse.ok(HttpHeader.of("Content-Type", mimeType), Arrays.toString(bytes));
    }
}
