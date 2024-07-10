package codesquad.webserver.http.type;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    SVG("svg", "image/svg+xml"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JS("js", "text/javascript"),
    ICO("ico", "image/x-icon"),
    JSON("json", "application/json"),
    XML("xml", "application/xml"),
    TXT("txt", "text/plain");

    private final String name;
    private final String mimeType;

    ContentType(String name, String type) {
        this.name = name;
        this.mimeType = type;
    }

    /**
     * 이름에 해당하는 ContentType 타입을 반환합니다.
     * @param name 파일 확장자에 해당하는 부분입니다. (e.g. *.html은 html)
     * @return ContentType
     */
    public static ContentType from(String name) {
        return Arrays.stream(ContentType.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 ContentType입니다. name: " + name));
    }

    public String getName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }
}
