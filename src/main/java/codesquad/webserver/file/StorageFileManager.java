package codesquad.webserver.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageFileManager {
    private static final Logger logger = LoggerFactory.getLogger(StorageFileManager.class);
    private static final String uploadDir = "uploads/";

    public static String saveFile(byte[] content, String fileExtension) {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID() + fileExtension;
        String filePath = uploadDir + fileName;

        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(content);
            logger.debug("파일을 저장했습니다. 경로: {}", filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    public static byte[] readFile(String filePath) {
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        File file = new File(filePath);
        byte[] fileContent = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("스토리지에서 파일을 읽어옵니다. 경로: {}", filePath);
        return fileContent;
    }

    public static String getRelativePath(String path) {
        return getUploadDir() + path;
    }

    private static String getUploadDir() {
        String currentDir = System.getProperty("user.dir");
        return currentDir + File.separator + uploadDir;
    }
}
