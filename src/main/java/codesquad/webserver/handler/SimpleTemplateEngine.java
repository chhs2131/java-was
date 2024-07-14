package codesquad.webserver.handler;

import codesquad.webserver.file.StaticFileReader;

import java.util.Map;

public class SimpleTemplateEngine {
    public static final StaticFileReader staticFileReader = new StaticFileReader();

    private SimpleTemplateEngine() {}

    // 템플릿 파일을 문자열로 읽는 메서드
    public static String readTemplate(String resourcePath) {
        return staticFileReader.read(resourcePath);
    }

    // 템플릿 문자열에서 플레이스홀더를 주어진 값으로 치환하는 메서드
    public static String processTemplate(String template, Map<String, String> values) {
        String processedTemplate = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            processedTemplate = processedTemplate.replace(placeholder, entry.getValue() != null ? entry.getValue() : "");
        }
        return processedTemplate;
    }
}
