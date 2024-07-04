package codesquad.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class Base64UtilTest {
    @Test
    @DisplayName("Base64 인코딩을 성공한다")
    public void testEncode() {
        byte[] data = "Hello, World!".getBytes();
        String expected = "SGVsbG8sIFdvcmxkIQ==";
        String actual = Base64Util.encode(data);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Base64 디코딩을 성공한다")
    public void testDecode() {
        String base64 = "SGVsbG8sIFdvcmxkIQ==";
        byte[] expected = "Hello, World!".getBytes();
        byte[] actual = Base64Util.decode(base64);
        assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Java Base64 Encoding and Decoding Test" ,
            "test.email@example.com" ,
            "안녕하세요, 무백스 화이팅!" ,
            "Special Characters: ~!@#$%^&*()_+{}|:\"<>?"
    })
    @DisplayName("Base64 인코딩과 디코딩의 결과가 동일하다")
    public void testEncodeAndDecode(String originalString) {
        byte[] originalBytes = originalString.getBytes();

        String encodedString = Base64Util.encode(originalBytes);
        byte[] decodedBytes = Base64Util.decode(encodedString);

        assertArrayEquals(originalBytes, decodedBytes);
        assertEquals(originalString, new String(decodedBytes));
    }

    @Test
    @DisplayName("문자열 인코딩에 성공한다")
    public void testEncodeToString() {
        String input = "Hello, World!";
        String expected = "SGVsbG8sIFdvcmxkIQ==";
        String actual = Base64Util.encodeToString(input);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("문자열 디코딩에 성공한다")
    public void testDecodeToString() {
        String base64 = "SGVsbG8sIFdvcmxkIQ==";
        String expected = "Hello, World!";
        String actual = Base64Util.decodeToString(base64);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("null이 입력되면 예외를 반환한다")
    public void testNullInput() {
        assertThrows(IllegalArgumentException.class, () -> Base64Util.encode((byte[]) null));
        assertThrows(IllegalArgumentException.class, () -> Base64Util.decode((String) null));
        assertThrows(IllegalArgumentException.class, () -> Base64Util.encodeToString(null));
        assertThrows(IllegalArgumentException.class, () -> Base64Util.decodeToString(null));
    }

    @Test
    @DisplayName("빈 문자열을 디코딩하면 예외를 반환한다")
    public void testEmptyStringInput() {
        assertThrows(IllegalArgumentException.class, () -> Base64Util.decode(""));
        assertThrows(IllegalArgumentException.class, () -> Base64Util.decodeToString(""));
    }
}