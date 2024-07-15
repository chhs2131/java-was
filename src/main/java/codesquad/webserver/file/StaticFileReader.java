package codesquad.webserver.file;

import codesquad.webserver.exception.OpenResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StaticFileReader {
    private static final Logger logger = LoggerFactory.getLogger(StaticFileReader.class);
    private static final String STATIC_FILE_PATH = "static";

    public String read(String path) {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(STATIC_FILE_PATH + path)) {
            return new String(resource.readAllBytes());
        } catch (IOException | NullPointerException e) {
            throw new OpenResourceException("파일을 읽는 중 오류가 발생했습니다. path: " + path);
        }
    }
}
