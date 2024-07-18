package codesquad.webserver.socket;

import codesquad.webserver.exception.SocketIoException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketReader {
    private static final int BUFFER_SIZE = 10000000;
    private final Socket socket;

    public SocketReader(Socket socket) {
        this.socket = socket;
    }

    /**
     * 전달된 Socket의 InputStream을 통해 값을 읽습니다.
     * @return byte 배열
     * @throws SocketIoException 소켓 IOException 발생시
     */
    public byte[] readBytes() {
        return getInputStreamBytes(socket);
    }

    private byte[] getInputStreamBytes(Socket clientSocket) {
        try {
            InputStream input = clientSocket.getInputStream();
            byte[] buffer = new byte[BUFFER_SIZE];

            ByteArrayOutputStream ba = new ByteArrayOutputStream();

            int bodyLength = -1;
            boolean headersParsed = false;
            StringBuilder headers = new StringBuilder();

            while (true) {
                int length = input.read(buffer);
                if (length == -1) {
                    break; // Stream 끝
                }
                ba.write(buffer, 0, length);

                if (!headersParsed) {
                    headers.append(new String(buffer, 0, length));
                    int headerEndIndex = headers.indexOf("\r\n\r\n");
                    if (headerEndIndex != -1) {
                        headersParsed = true;
                        String headerSection = headers.substring(0, headerEndIndex + 4);

                        // Content-Length 길이를 Header로 부터 가져옴
                        Pattern pattern = Pattern.compile("(?i)Content-Length:\\s*(\\d+)");
                        Matcher matcher = pattern.matcher(headerSection);
                        if (matcher.find()) {
                            bodyLength = Integer.parseInt(matcher.group(1));
                        }
                    }
                }

                // 헤더를 모두 읽었고 && Content-Length가 없을 때 종료
                if (headersParsed && bodyLength == -1) {
                    break;
                }
                // 바디까지 모두 읽은 경우 종료
                if (ba.size() >= headers.indexOf("\r\n\r\n") + 4 + bodyLength) {
                    break;
                }
            }

            return ba.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SocketIoException("Socket 연결이 불안정합니다.");
        }
    }
}
