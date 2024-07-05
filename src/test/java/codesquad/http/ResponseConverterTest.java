package codesquad.http;

import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResponseConverterTest {
    @Test
    @DisplayName("HttpResponse를 성공적으로 변환한다")
    public void test_converts_valid_http_response_with_headers_and_body_to_byte_array() {
        HttpResponse response = new HttpResponse(
                HttpProtocol.HTTP_1_1,
                HttpStatus.OK,
                Map.of("Content-Type", "text/plain"),
                "Hello, World!"
        );

        byte[] result = ResponseConverter.toSocketBytes(response);

        String expected = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHello, World!";
        assertArrayEquals(expected.getBytes(), result);
    }

    @Test
    @DisplayName("NO_CONTENT를 성공적으로 변환한다")
    public void test_converts_valid_http_response_with_headers_but_no_body_to_byte_array() {
        HttpResponse response = new HttpResponse(
                HttpProtocol.HTTP_1_1,
                HttpStatus.NO_CONTENT,
                Map.of("Content-Type", "text/plain"),
                ""
        );

        byte[] result = ResponseConverter.toSocketBytes(response);

        String expected = "HTTP/1.1 204 No Content\r\nContent-Type: text/plain\r\n";
        assertArrayEquals(expected.getBytes(), result);
    }

    @Test
    @DisplayName("null이 전달되면 Exception이 발생한다.")
    public void test_handles_null_http_response_gracefully() {
        HttpResponse response = null;

        assertThrows(IllegalArgumentException.class, () -> {
            ResponseConverter.toSocketBytes(response);
        });
    }

    @Test
    @DisplayName("header가 비어있어도 성공적으로 변환한다.")
    public void test_handles_http_response_with_empty_headers_map() {
        HttpResponse response = new HttpResponse(
                HttpProtocol.HTTP_1_1,
                HttpStatus.OK,
                Map.of(),
                "Hello, World!"
        );

        byte[] result = ResponseConverter.toSocketBytes(response);

        String expected = "HTTP/1.1 200 OK\r\n\r\nHello, World!";
        assertArrayEquals(expected.getBytes(), result);
    }
}
