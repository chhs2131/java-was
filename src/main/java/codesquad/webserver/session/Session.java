package codesquad.webserver.session;

import java.time.LocalDateTime;
import java.util.Map;

public record Session(
        String id,
        LocalDateTime expired,
        Map<String, Object> attributes
) {
}
