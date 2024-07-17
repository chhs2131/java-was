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

    public static FormEnctype from(String value) {
        if (value == null) {
            return NOT_FORM_DATA;
        }
        return Arrays.stream(FormEnctype.values())
                .filter(enctype -> value.contains(enctype.value))
                .findFirst()
                .orElse(NOT_FORM_DATA);
    }

    public String getValue() {
        return value;
    }
}
