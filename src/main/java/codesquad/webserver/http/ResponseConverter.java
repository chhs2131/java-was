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
        String responseLine = response.getProtocol() + " " + response.getStatus().getCode() + " " + response.getStatus().getMessage() + "\r\n";
        sb.append(responseLine);

        // Headers
        if (response.getHeaders() != null) {
            for (Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
                for (String value : entry.getValue()) {
                    String header = entry.getKey() + ": " + value + "\r\n";
                    sb.append(header);
                }
            }
        }

        // Body
        if ((response.getBodyBytes() != null && response.getBodyBytes().length != 0) ||
                (response.getBody() != null && !response.getBody().isEmpty())) {
            sb.append("\r\n");

            if (!response.isBytes()) {  // 일반 String Type Body인 경우
                sb.append(response.getBody());
            }
        }

        byte[] bytes;
        try {
            bytes = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8를 사용할 수 없습니다.", e);
        }

        // Bytes Type Body
        if (response.isBytes()) {
            bytes = concatenateByteArrays(bytes, response.getBodyBytes());
        }
        return bytes;
    }

    public static byte[] concatenateByteArrays(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
}
