package codesquad.webserver.http.parser;

import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.type.FormEnctype;
import codesquad.webserver.http.type.HttpHeader;
import codesquad.webserver.http.type.StartLine;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    private HttpRequestParser() {}

    public static HttpRequest parse(String message) {
        String[] lines = message.split("\n");

        StartLine startLine = StartLineParser.parse(lines);
        HttpHeader headers = HeaderParser.parse(lines);

        // Body
        int contentLength = getContentLength(headers);
        Map<String, String> body = new HashMap<>();
        switch (getFormEnctype(headers)) {
            case X_WWW_FORM_URLENCODED -> body = BodyParser.pasreFormXwww(lines, contentLength);
            case MULTIPART_FORM_DATA -> body = BodyParser.parseFormMultiPart(lines, contentLength);
            case TEXT_PLAIN -> body.put("raw", BodyParser.parse(lines, contentLength));
            case NOT_FORM_DATA -> body.put("raw", BodyParser.parse(lines, contentLength));
        }

        return new HttpRequest(
                startLine.method(),
                startLine.path(),
                startLine.query(),
                startLine.protocol(),
                headers,
                body);
    }

    private static int getContentLength(HttpHeader headers) {
        int contentLength = 0;
        if (headers.contains("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
        }
        return contentLength;
    }

    private static FormEnctype getFormEnctype(HttpHeader headers) {
        if (!headers.contains("Content-Type")) {
            return FormEnctype.NOT_FORM_DATA;
        }
        return FormEnctype.from(headers.get("Content-Type"));
    }
}
