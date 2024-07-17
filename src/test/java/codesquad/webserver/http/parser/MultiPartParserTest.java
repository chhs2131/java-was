package codesquad.webserver.http.parser;

import static codesquad.webserver.http.parser.MultiPartParser.parse;

import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultiPartParserTest {
    @Test
    @DisplayName("멀티파트 파싱을 정상적으로 수행한다.")
    public void test_parse() {
        byte[] bytes = ("POST /upload HTTP/1.1\r\n" +
            "Host: example.com\r\n" +
            "Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryABC123\r\n" +
            "\r\n" +
            "------WebKitFormBoundaryABC123\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"example.txt\"\r\n" +
            "Content-Type: text/plain\r\n" +
            "\r\n" +
            "This is the content of the file.\r\n" +
            "And here is the second line.\r\n" +
            "------WebKitFormBoundaryABC123--\r\n").getBytes();

        Map<String, String> parts = parse(bytes, "----WebKitFormBoundaryABC123");
        for (Map.Entry<String, String> entry : parts.entrySet()) {
            Assertions.assertThat(entry.getKey().toString()).isEqualTo("file");
            Assertions.assertThat(entry.getValue().toString()).startsWith("uploads/");
        }
    }
}