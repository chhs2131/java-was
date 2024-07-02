package codesquad.util;

public class StringUtil {
    private StringUtil() {}

    public static String getExtension(String path) {
        String[] split = path.split("\\.");
        if (split.length == 0) {
            return "";
        }

        return split[split.length - 1];
    }
}
