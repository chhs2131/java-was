package codesquad.http.parser;

import codesquad.http.HttpRequest;
import codesquad.http.type.StartLine;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    private HttpRequestParser() {}

    public static HttpRequest parse(String message) {
        String[] lines = message.split("\n");

        StartLine startLine = StartLineParser.parse(lines);
        Map<String, String> headers = HeaderParser.parse(lines);

        // Body
        int contentLength = getContentLength(headers);
        Map<String, String> body = new HashMap<>();
        if (isFormUrlencoded(headers)) {
            body = BodyParser.parseFormBody(lines, contentLength);
        } else {
            String rawBody = BodyParser.parse(lines, contentLength);
            body.put("raw", rawBody);
        }

        return new HttpRequest(
                startLine.method(),
                startLine.path(),
                startLine.query(),
                startLine.protocol(),
                headers,
                body);
    }

    private static int getContentLength(Map<String, String> headers) {
        int contentLength = 0;
        if (headers.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
        }
        return contentLength;
    }

    private static boolean isFormUrlencoded(Map<String, String> headers) {
        if (!headers.containsKey("Content-Type")) {
            return false;
        }
        return headers.get("Content-Type").equals("application/x-www-form-urlencoded");
    }
}
