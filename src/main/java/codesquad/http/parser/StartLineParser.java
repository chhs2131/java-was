package codesquad.http.parser;

import codesquad.http.type.HttpMethod;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.StartLine;

import java.util.HashMap;
import java.util.Map;

public class StartLineParser {
    private StartLineParser() {}

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
        return KeyValueParser.parseQuertString(queryString);
    }
}
