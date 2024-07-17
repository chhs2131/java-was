package codesquad.webserver.http.type;

import java.util.Arrays;

public enum FormEnctype {
    X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    TEXT_PLAIN("text/plain"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    NOT_FORM_DATA("");

    private final String value;

    FormEnctype(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isMultipartForm() {
        return this == MULTIPART_FORM_DATA;
    }

    public static FormEnctype from(String value) {
        if (value == null) {
            return NOT_FORM_DATA;
        }
        return Arrays.stream(FormEnctype.values())
                .filter(enctype -> value.contains(enctype.value))
                .findFirst()
                .orElse(NOT_FORM_DATA);
    }

    public static String getBoundary(String value) {
        if (!from(value).isMultipartForm()) {
            throw new IllegalArgumentException("멀티파트 헤더가 아닙니다. header: " + value);
        }

        String[] split = value.split("boundary=");
        if (split.length != 2) {
            throw new IllegalArgumentException("값을 찾을 수 없습니다. split길이: " + split.length);
        }

        return "--" + split[1];
    }
}
