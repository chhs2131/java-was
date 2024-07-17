package codesquad.webserver;

import codesquad.webserver.handler.StaticRequestHandler;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.http.type.HttpProtocol;
import codesquad.webserver.http.type.HttpStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestHandlerTest {
    private RequestHandler requestHandler;

    @BeforeEach
    public void setUp() {
        requestHandler = new RequestHandler(List.of(new StaticRequestHandler()));
    }

    @Test
    @DisplayName("요청에 해당하는 것이 없는 경우 BadRequest 반환")
    public void test_handles_request_for_static_file_with_no_extension() {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/file", null, HttpProtocol.HTTP_1_1, null, null);

        HttpResponse httpResponse = requestHandler.handle(httpRequest);

        assertEquals(HttpStatus.BAD_REQUEST, httpResponse.getStatus());
    }

    @Test
    @DisplayName("해당하는 정적리소스를 반환할 수 없다면 NotFound 반환")
    public void test_handles_request_for_static_file_with_no_extension2() {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/file.html", null, HttpProtocol.HTTP_1_1, null, null);

        HttpResponse httpResponse = requestHandler.handle(httpRequest);

        assertEquals(HttpStatus.NOT_FOUND, httpResponse.getStatus());
    }
}
