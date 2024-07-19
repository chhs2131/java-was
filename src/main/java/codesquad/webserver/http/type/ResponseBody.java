package codesquad.webserver.http.type;

public class ResponseBody {
    private final String body;
    private final byte[] bytes;
    private final boolean isBytes;

    public ResponseBody(String body) {
        this.body = body;
        this.bytes = new byte[0];
        this.isBytes = false;
    }

    public ResponseBody(byte[] bytes) {
        this.bytes = bytes;
        this.body = "";
        this.isBytes = true;
    }

    public String getBody() {
        return body;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public boolean isBytes() {
        return isBytes;
    }
}
