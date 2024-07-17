package codesquad.webserver.http.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class BodyParser {
    private BodyParser() {}

    public static String parse(String[] lines, int contentLength) {
        return extractBody(lines, contentLength);
    }

    public static Map<String, String> parseFormMultiPart(String[] lines, int contentLength, String boundary) {
        String body = extractBodyOnlyLength(lines, contentLength);
        String[] multiPart = body.split(boundary);

        for (String s : multiPart) {
            System.out.println("Target: " + s);
            parseMultipartData(s);
        }

        throw new UnsupportedOperationException();
    }

    private static void parseMultipartData(String line) {
        Map<String, String> multiPart = KeyValueParser.parseMultiPart(line);
        System.out.println("헤더반환: " + multiPart);

        // Header

        // Body

    }

    private static void saveFile(byte[] content, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(new File(fileName))) {
            fos.write(content);
            System.out.println("File saved: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static Map<String, String> pasreFormXwww(String[] lines, int contentLength) {
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

    private static String extractBodyOnlyLength(String[] lines, int contentLength) {
        StringBuilder body = new StringBuilder();

        int bodyStartIndex = getBodyStartIndex(lines);
        int currentLength = 0;

        for (int i = bodyStartIndex; i < lines.length; i++) {
            String line = lines[i];
            int lineLength = line.length() + 1; // '\n' 문자를 고려하여 1을 더함

            if (currentLength + lineLength > contentLength) {
                int remainingLength = contentLength - currentLength;
                body.append(line, 0, Math.min(remainingLength, line.length()));
                if (remainingLength > line.length()) {
                    body.append('\n');
                }
                break;
            } else {
                body.append(line).append('\n');
                currentLength += lineLength;
            }
        }

        if (body.length() > 0 && body.charAt(body.length() - 1) == '\n') {
            body.deleteCharAt(body.length() - 1);
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
