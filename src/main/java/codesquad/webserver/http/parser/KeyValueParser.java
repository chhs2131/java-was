package codesquad.webserver.http.parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class KeyValueParser {
    private KeyValueParser() {}

    public static Map<String, String> parse(String body) {
        return parseQuertString(body);
    }

    public static Map<String, String> parseQuertString(String body) {
        Map<String, String> query = new HashMap<>();
        for (String pair : body.split("&")) {
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
                throw new IllegalArgumentException("UTF-8을 지원하지 않습니다.");
            }
        }
        return query;
    }

    public static Map<String, String> parseCookie(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return cookies;
        }

        String[] pairs = cookieHeader.split("; ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0], keyValue[1]);
            }
        }

        return cookies;
    }


    public static Map<String, String> parseMultiPart(String multiPartHeader) {
        Map<String, String> cookies = new HashMap<>();

        if (multiPartHeader == null || multiPartHeader.isEmpty()) {
            return cookies;
        }

        String[] pairs = multiPartHeader.split("; ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("[=]|(: )", 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        return cookies;
    }
}
