package codesquad.webserver.handler;

import codesquad.webserver.file.StorageFileManager;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.*;
import codesquad.webserver.util.StringUtil;

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

        HttpHeader httpHeader = HttpHeader.of("Content-Type", mimeType, "Content-Length", String.valueOf(bytes.length));
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.OK, httpHeader, bytes);
    }
}
