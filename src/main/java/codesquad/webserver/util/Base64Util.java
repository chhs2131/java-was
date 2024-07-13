package codesquad.webserver.util;

import java.io.UnsupportedEncodingException;

import static java.util.Base64.*;

public class Base64Util {
    private Base64Util() {}

    public static String encode(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Input byte array cannot be null");
        }
        return getEncoder().encodeToString(bytes);
    }

    public static byte[] decode(String base64) {
        if (base64 == null || base64.isEmpty()) {
            throw new IllegalArgumentException("Input Base64 string cannot be null or empty");
        }
        return getDecoder().decode(base64);
    }

    public static String encodeToString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        try {
            return encode(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8를 사용할 수 없습니다.", e);
        }
    }

    public static String decodeToString(String base64) {
        if (base64 == null || base64.isEmpty()) {
            throw new IllegalArgumentException("Input Base64 string cannot be null or empty");
        }
        try {
            return new String(decode(base64), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8를 사용할 수 없습니다.", e);
        }
    }
}
