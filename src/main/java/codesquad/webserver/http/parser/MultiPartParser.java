package codesquad.webserver.http.parser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiPartParser {
    private static final Logger logger = LoggerFactory.getLogger(MultiPartParser.class);

    private MultiPartParser() {}

    public static Map<String, String> parse(byte[] bytes, String boundary) {
        final byte[] multiPartBody = extractBody(bytes);
        List<byte[]> parts = splitMultiPartBody(multiPartBody, boundary);

        Map<String, String> partContents = new HashMap<>();
        for (byte[] part : parts) {
            Map<String, String> partMap = parsePart(part);
            partContents.putAll(partMap);
        }

        return partContents;
    }

    /**
     * Bytes에서 Body 부분만 추출합니다.
     * @param bytes 전체 HTTP Request 배열
     * @return HTTP Request에서 Body만 추출한 부분
     */
    private static byte[] extractBody(final byte[] bytes) {
        // Find the position of the empty line (CRLF 2 times)
        int bodyStartIndex = findBodyStart(bytes);

        if (bodyStartIndex == -1) {
            throw new IllegalArgumentException("Invalid HTTP request format: body not found");
        }

        return Arrays.copyOfRange(bytes, bodyStartIndex, bytes.length);
    }

    private static int findBodyStart(byte[] bytes) {
        for (int i = 0; i < bytes.length - 3; i++) {
            if (bytes[i] == '\r' && bytes[i + 1] == '\n' && bytes[i + 2] == '\r' && bytes[i + 3] == '\n') {
                return i + 4; // Body starts after "\r\n\r\n"
            }
        }
        return -1; // Not found
    }

    /**
     * MultiPart의 Body 부분들을 나눠서 반환합니다.
     * @param multiPartBody MultiPart Body 부분 전체
     * @param boundaryStr 구분자로 사용될 boundary 값
     * @return 구분된 Body들
     */
    private static List<byte[]> splitMultiPartBody(byte[] multiPartBody, String boundaryStr) {
        List<byte[]> parts = new ArrayList<>();

        // Split multiPartBody by boundary
        byte[] boundaryBytes = boundaryStr.getBytes();

        int boundaryIndex = indexOf(multiPartBody, boundaryBytes, 0);
        while (boundaryIndex != -1) {
            int partStartIndex = boundaryIndex + boundaryBytes.length;
            int nextBoundaryIndex = indexOf(multiPartBody, boundaryBytes, partStartIndex);

            if (nextBoundaryIndex == -1) {
                // Last part
                parts.add(Arrays.copyOfRange(multiPartBody, partStartIndex, multiPartBody.length));
                break;
            } else {
                parts.add(Arrays.copyOfRange(multiPartBody, partStartIndex, nextBoundaryIndex - 2)); // -2 to exclude CRLF after boundary
            }

            boundaryIndex = nextBoundaryIndex;
        }

        return parts;
    }

    private static int indexOf(byte[] source, byte[] target, int fromIndex) {
        int max = source.length - target.length;
        for (int i = fromIndex; i <= max; i++) {
            int j;
            for (j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    break;
                }
            }
            if (j == target.length) {
                return i;
            }
        }
        return -1;
    }

    /**
     * MultiPart의 한 부분을 전달하여 Body값을 추출합니다. 전달된 것이 파일인 경우 내용으 저장하고 Path를 반환합니다.
     * @param part MultiPart 중 한 부분
     * @return Body값 (name과 value)
     */
    private static Map<String, String> parsePart(byte[] part) {
        Map<String, String> partMap = new HashMap<>();

        String partString = new String(part);
        if (partString.trim().equals("--")) {
            return partMap;
        }

        // Header와 Body로 구분
        int headerEndIndex = indexOf(part, "\r\n\r\n".getBytes(), 0);
        if (headerEndIndex == -1) {
            throw new IllegalArgumentException("Invalid part format: headers not found");
        }

        byte[] headersBytes = Arrays.copyOfRange(part, 0, headerEndIndex);
        String headers = new String(headersBytes);

        Map<String, String> headersMap = parseHeaders(headers);

        // 파일인 경우 저장하고 Path를 반환 또는 Body값을 반환
        String contentDisposition = headersMap.get("Content-Disposition");
        if (contentDisposition != null && contentDisposition.startsWith("form-data")) {
            String[] dispositionParts = contentDisposition.split(";");

            String name = null;
            String filename = null;

            for (String partHeader : dispositionParts) {
                partHeader = partHeader.trim();
                if (partHeader.startsWith("name=")) {
                    name = partHeader.substring(5).replaceAll("\"", "");
                } else if (partHeader.startsWith("filename=")) {
                    filename = partHeader.substring(10).replaceAll("\"", "");
                }
            }

            if (filename != null) {
                // This part contains a file
                String fileExtension = getFileExtension(filename);
                String filePath = saveFile(Arrays.copyOfRange(part, headerEndIndex + 4, part.length), fileExtension);
                partMap.put(name, filePath);
            } else {
                // Handle other form data
                byte[] bodyBytes = Arrays.copyOfRange(part, headerEndIndex + 4, part.length); // +4 to skip "\r\n\r\n"
                String body = new String(bodyBytes);
                partMap.put(name, body);
            }
        }

        return partMap;
    }

    private static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }

    private static String saveFile(byte[] content, String fileExtension) {
        String uploadDir = "uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = uploadDir + fileName;

        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(content);
            logger.debug("파일을 저장했습니다. 경로: {}", filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    private static Map<String, String> parseHeaders(String headers) {
        Map<String, String> headersMap = new HashMap<>();
        String[] headerLines = headers.split("\r\n");

        for (String headerLine : headerLines) {
            int colonIndex = headerLine.indexOf(':');
            if (colonIndex != -1) {
                String key = headerLine.substring(0, colonIndex).trim();
                String value = headerLine.substring(colonIndex + 1).trim();
                headersMap.put(key, value);
            }
        }
        return headersMap;
    }
}
