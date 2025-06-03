package se.ox.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private HttpStatus status = HttpStatus.OK;
    private final OutputStream output;
    private final Map<String, String> headers = new HashMap<>();

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public OutputStream getOutput() {
        return output;
    }

    public void send(String body) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(status.getStatusCode()).append(" ").append(status).append("\r\n");
        response.append("Content-Type: application/json\r\n");
        headers.forEach((k, v) -> response.append(k).append(": ").append(v).append("\r\n"));
        byte[] bodyBytes = body.getBytes();
        response.append("Content-Length: ").append(bodyBytes.length).append("\r\n\r\n");
        output.write(response.toString().getBytes());
        output.write(bodyBytes);
        output.flush();
    }
}
