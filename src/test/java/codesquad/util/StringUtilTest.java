package codesquad.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {
    @Test
    @DisplayName("확장자를 성공적으로 가져온다.")
    public void test_returns_correct_extension_for_simple_file_path() {
        String path = "file.txt";

        String extension = StringUtil.getExtension(path);

        assertEquals("txt", extension);
    }
}
