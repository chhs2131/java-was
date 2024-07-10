package codesquad.webserver.http;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map.Entry;

public class ResponseConverter {
    private ResponseConverter() {}

    public static byte[] toSocketBytes(HttpResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("null은 bytes로 변환할 수 없습니다.");
        }

        StringBuilder sb = new StringBuilder();

        // StartLine
        String responseLine = response.protocol() + " " + response.status().getCode() + " " + response.status().getMessage() + "\r\n";
        sb.append(responseLine);

        // Headers
        if (response.headers() != null) {
            for (Entry<String, List<String>> entry : response.headers().entrySet()) {
                for (String value : entry.getValue()) {
                    String header = entry.getKey() + ": " + value + "\r\n";
                    sb.append(header);
                }
            }
        }

        // Body
        if (response.body() != null && !response.body().isEmpty()) {
            sb.append("\r\n");
            sb.append(response.body());
        }

        try {
            return sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8를 사용할 수 없습니다.", e);
        }
    }
}
