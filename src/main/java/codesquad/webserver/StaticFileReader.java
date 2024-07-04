package codesquad.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StaticFileReader {
    private static final Logger logger = LoggerFactory.getLogger(StaticFileReader.class);
    private static final String STATIC_FILE_PATH = "src/main/resources/static";

    public String read(String path) {
        String filePath = STATIC_FILE_PATH + path;

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다. path: " + filePath);
        }

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            byte[] fileBytes = fileInputStream.readAllBytes();
            return new String(fileBytes, "UTF-8");
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다. path: " + filePath, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽는 중 오류가 발생했습니다. path: " + filePath, e);
        }
    }
}
