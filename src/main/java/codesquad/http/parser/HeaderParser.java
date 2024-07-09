package codesquad.http.parser;

import java.util.HashMap;
import java.util.Map;

public class HeaderParser {
    private HeaderParser() {}

    public static Map<String, String> parse(String[] lines) {
        Map<String, String> headers = new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                break;
            }
            String[] header = lines[i].split(": ", 2); // 헤더 값을 안전하게 파싱하기 위해 split 제한을 둠
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
            }
        }

        return headers;
    }
}
