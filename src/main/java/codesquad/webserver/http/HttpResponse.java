package codesquad.webserver.http;

import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.HttpProtocol;
import codesquad.webserver.http.type.HttpStatus;
import codesquad.webserver.http.type.ResponseBody;

public class HttpResponse {
    private final HttpProtocol protocol;
    private final HttpStatus status;
    private final HttpHeader headers;
    private final ResponseBody body;

    public HttpResponse(final HttpProtocol protocol, final HttpStatus status, final HttpHeader headers,
                        final String body) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.body = new ResponseBody(body);
    }

    public HttpResponse(final HttpProtocol protocol, final HttpStatus status, final HttpHeader headers,
                        final byte[] bodyBytes) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.body = new ResponseBody(bodyBytes);
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
        return body.getBody();
    }

    public byte[] getBodyBytes() {
        return body.getBytes();
    }

    public boolean isBytes() {
        return body.isBytes();
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
