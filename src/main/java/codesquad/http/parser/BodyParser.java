package codesquad.http.parser;

import java.util.List;

public class BodyParser {
    public static String parse(String[] lines, int contentLength) {
        StringBuilder body = new StringBuilder();

        int bodyStartIndex = 0;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) {
                bodyStartIndex = i + 1;
                break;
            }
        }

        for (int i = bodyStartIndex; i < lines.length; i++) {
            body.append(lines[i]).append("\n");
        }

        if (!body.isEmpty() && body.charAt(body.length() - 1) == '\n') {
            body.deleteCharAt(body.length() - 1);
        }

        // Content-Length를 참고하여 본문을 자릅니다.
        if (body.length() > contentLength) {
            body.setLength(contentLength);
        }

        return body.toString();
    }

    public static List<String> parseFormBody(String[] lines, int contentLength) {
        throw new UnsupportedOperationException();
    }
}
