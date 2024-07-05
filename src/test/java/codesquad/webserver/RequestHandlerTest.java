package codesquad.webserver;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.type.HttpMethod;
import codesquad.http.type.HttpProtocol;
import codesquad.http.type.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestHandlerTest {
    @Test
    @DisplayName("요청에 해당하는 것이 없는 경우 BadRequest 반환")
    public void test_handles_request_for_static_file_with_no_extension() {
        // 헤더가 존재하지 않는 요청은 뭐ㅏ고 해석해야하지?
        RequestHandler requestHandler = new RequestHandler();
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/file", null, HttpProtocol.HTTP_1_1, null, null);

        HttpResponse httpResponse = requestHandler.handle(httpRequest);

        assertEquals(HttpStatus.BAD_REQUEST, httpResponse.status());
    }

    @Test
    @DisplayName("해당하는 정적리소스를 반환할 수 없다면 NotFound 반환")
    public void test_handles_request_for_static_file_with_no_extension2() {
        // 헤더가 존재하지 않는 요청은 뭐ㅏ고 해석해야하지?
        RequestHandler requestHandler = new RequestHandler();
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/file.html", null, HttpProtocol.HTTP_1_1, null, null);

        HttpResponse httpResponse = requestHandler.handle(httpRequest);

        assertEquals(HttpStatus.NOT_FOUND, httpResponse.status());
    }

    @Test
    @DisplayName("요청에 해당하는 파일이 있는 경우 Ok 반환")
    public void test_returns_ok_for_valid_static_file_request() {
        // 먼 훗날 파일이 사라지면 어떻게하지? 테스트용 파일을 만들어야하나?
        RequestHandler requestHandler = new RequestHandler();
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/index.html", null, HttpProtocol.HTTP_1_1, null, null);

        HttpResponse httpResponse = requestHandler.handle(httpRequest);

        assertEquals(HttpStatus.OK, httpResponse.status());
    }
}
