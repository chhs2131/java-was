package codesquad.http.parser;

import codesquad.http.type.HttpHeader;

public class HeaderParser {
    private HeaderParser() {}

    public static HttpHeader parse(String[] lines) {
        final HttpHeader httpHeader = new HttpHeader();

        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                break;
            }
            String[] header = lines[i].split(": ", 2); // 헤더 값을 안전하게 파싱하기 위해 split 제한을 둠
            if (header.length == 2) {
                httpHeader.add(header[0].trim(), header[1].trim());
            }
        }

        return httpHeader;
    }
}
