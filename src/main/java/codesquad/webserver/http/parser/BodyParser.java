package codesquad.webserver.http.parser;

import java.util.Map;

public class BodyParser {
    private BodyParser() {}

    public static String parse(String[] lines, int contentLength) {
        return extractBody(lines, contentLength);
    }

    public static Map<String, String> parseFormBody(String[] lines, int contentLength) {
        String body = extractBody(lines, contentLength);
        return KeyValueParser.parseQuertString(body);
    }

    private static String extractBody(String[] lines, int contentLength) {
        StringBuilder body = new StringBuilder();

        int bodyStartIndex = getBodyStartIndex(lines);
        for (int i = bodyStartIndex; i < lines.length; i++) {
            body.append(lines[i]).append("\n");
        }

        if (!body.isEmpty() && body.charAt(body.length() - 1) == '\n') {
            body.deleteCharAt(body.length() - 1);
        }

        if (body.length() > contentLength) {
            body.setLength(contentLength);
        }

        return body.toString();
    }

    private static int getBodyStartIndex(String[] lines) {
        int bodyStartIndex = 0;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) {
                bodyStartIndex = i + 1;
                break;
            }
        }
        return bodyStartIndex;
    }
}
