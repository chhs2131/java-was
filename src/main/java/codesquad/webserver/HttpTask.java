package codesquad.webserver;

import codesquad.application.User;
import codesquad.webserver.authentication.AuthenticationHolder;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.parser.HttpRequestParser;
import codesquad.webserver.http.HttpResponse;
import codesquad.webserver.http.ResponseConverter;
import codesquad.webserver.http.type.Cookie;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import codesquad.webserver.socket.SocketReader;
import codesquad.webserver.socket.SocketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

/**
 * 전달 받은 Socket을 기반으로 HTTP 요청 해석 및 적절한 응답을 진행합니다.
 * @param clientSocket 요청 확인 및 응답을 진행할 클라이언트 소켓
 * @param requestHandler HTTP Request를 해석하고 Response를 반환하는 객체
 */
public record HttpTask(Socket clientSocket, RequestHandler requestHandler) implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HttpTask.class);

    @Override
    public void run() {
        try {
            SocketReader socketReader = new SocketReader(clientSocket);
            SocketWriter socketWriter = new SocketWriter(clientSocket);
            String message = socketReader.read();

            HttpRequest request = HttpRequestParser.parse(message);
            logger.debug("HTTP Request: {} {} {}", request.method(), request.path(), request.protocol());
            logger.debug("HTTP Headers: size: {}", request.headers().size());
            logger.debug("HTTP Body: {}", request.body());


            SessionFilter sessionFilter = new SessionFilter();
            if (sessionFilter.support()) {
                sessionFilter.doFilter(request);
            }


            HttpResponse response = requestHandler.handle(request);
            byte[] socketBytes = ResponseConverter.toSocketBytes(response);
            socketWriter.write(socketBytes);

            clientSocket.close();
        } catch (IOException e) {
            logger.error("요청 처리를 실패했습니다. {}", e.getMessage());
            e.printStackTrace();
        }
    }

    ////////////////// TODO Filter 흉내내기!
    private static class SessionFilter {
        private static final SessionManager sessionManager = new SessionManager();

        public boolean support() {
            return true;
        }

        public void doFilter(HttpRequest httpRequest) {
            // ThreadLocal 초기화
            AuthenticationHolder.clear();

            // 세션 정보 가져오기
            Cookie cookies = httpRequest.headers().getCookies();
            String sessionId = cookies.get("SID");

            // 세션이 정상인지 확인
            if (sessionId == null || !sessionManager.validSession(sessionId)) {
                return;
            }

            // 정상인 경우 ThreadLocal에 이를 저장
            Optional<Session> session = sessionManager.getSession(sessionId);
            User user = (User) session.get().attributes().get("user");
            AuthenticationHolder.setContext(user);

            // TODO 끝나고 ThreadLocal 비우기
        }
    }
}
