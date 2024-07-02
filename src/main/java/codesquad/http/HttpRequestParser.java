package codesquad.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    private HttpRequestParser() {}

    public static HttpRequest parse(String message) {
        String[] lines = message.split("\n");

        String[] startLine = lines[0].split(" ");
        String method = startLine[0];
        String path = startLine[1];
        String protocol = startLine[2];

        Map<String, String> headers = parseHeaders(lines);

        int contentLength = 0;
        if (headers.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
        }
        String body = parseBody(lines, contentLength);

        return new HttpRequest(method, path, protocol, headers, body);
    }

    private static Map<String, String> parseHeaders(String[] lines) {
        Map<String, String> headers = new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                break;
            }

            String[] header = lines[i].split(": ", 2); // 헤더 값을 안전하게 파싱하기 위해 split 제한을 둠
            if (header.length == 2) {
                headers.put(header[0], header[1]);
            }
        }

        return headers;
    }

    private static String parseBody(String[] lines, int contentLength) {
        StringBuilder body = new StringBuilder();

        int bodyStartIndex = 0;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                bodyStartIndex = i + 1;
                break;
            }
        }

        for (int i = bodyStartIndex; i < lines.length; i++) {
            body.append(lines[i]).append("\n");
        }

        if (body.length() > 0 && body.charAt(body.length() - 1) == '\n') {
            body.deleteCharAt(body.length() - 1);
        }

        // Content-Length를 참고하여 본문을 자릅니다.
        if (body.length() > contentLength) {
            body.setLength(contentLength);
        }

        return body.toString();
    }
}
