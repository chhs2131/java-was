package codesquad.webserver.http;

import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpProtocol;
import codesquad.webserver.http.type.HttpStatus;

public class HttpResponse {
    private final HttpProtocol protocol;
    private final HttpStatus status;
    private final HttpHeader headers;
    private final String body;  // TODO ResponseBody 타입으로 합치기
    private final byte[] bodyBytes;
    private final boolean isBytes;

    public HttpResponse(final HttpProtocol protocol, final HttpStatus status, final HttpHeader headers,
                        final String body) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.body = body;

        bodyBytes = new byte[0];
        isBytes = false;
    }

    public HttpResponse(final HttpProtocol protocol, final HttpStatus status, final HttpHeader headers,
                        final byte[] bodyBytes) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.bodyBytes = bodyBytes;

        body = "";
        isBytes = true;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public HttpHeader getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public boolean isBytes() {
        return isBytes;
    }

    public static HttpResponse ok(HttpHeader headers, String body) {
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.OK, headers, body);
    }

    public static HttpResponse notFound(String body) {
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.NOT_FOUND, HttpHeader.createEmpty(), body);
    }

    public static HttpResponse badRequest(String body) {
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.BAD_REQUEST, HttpHeader.createEmpty(), body);
    }

    public static HttpResponse found(String redirectLocation, String body) {
        HttpHeader headers = HttpHeader.createRedirection(redirectLocation);
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.FOUND, headers, body);
    }

    public static HttpResponse unauthorized(String body) {
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.UNAUTHORIZED, HttpHeader.createEmpty(), body);
    }

    public static HttpResponse internalServerError(String body) {
        return new HttpResponse(HttpProtocol.HTTP_1_1, HttpStatus.INTERNAL_SERVER_ERROR, HttpHeader.createEmpty(), body);
    }
}
