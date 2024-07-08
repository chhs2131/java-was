package codesquad.http.parser;

import codesquad.http.type.HttpMethod;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.StartLine;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class StartLineParser {
    public static StartLine parse(String[] lines) {
        String[] startLine = lines[0].split(" ");
        HttpMethod method = HttpMethod.from(startLine[0]);
        String[] pathAndQueryString = startLine[1].split("\\?");
        String path = pathAndQueryString[0];
        Map<String, String> query = new HashMap<>();
        if (pathAndQueryString.length > 1) {
            query = parseQueryString(pathAndQueryString[1]);
        }
        HttpProtocol protocol = HttpProtocol.from(startLine[2].trim());

        return new StartLine(method, path, query, protocol);
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> query = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=");

            try {
                if (keyValue.length == 2) {
                    String key = URLDecoder.decode(keyValue[0], "UTF-8");
                    String value = URLDecoder.decode(keyValue[1], "UTF-8");
                    query.put(key, value);
                } else {  // QueryString value값이 비어있는 경우
                    String key = URLDecoder.decode(keyValue[0], "UTF-8");
                    query.put(key, "");
                }
            } catch (UnsupportedEncodingException e) {
                new IllegalArgumentException("UTF-8을 지원하지 않습니다.");
            }
        }
        return query;
    }
}
