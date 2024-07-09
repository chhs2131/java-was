package codesquad.http.parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class KeyValueParser {
    private KeyValueParser() {};

    public static Map<String, String> parse(String body) {
        return getStringStringMap(body);
    }

    public static Map<String, String> getStringStringMap(String body) {
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
                new IllegalArgumentException("UTF-8을 지원하지 않습니다.");
            }
        }
        return query;
    }
}
