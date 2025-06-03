package se.ox.helper;

import se.ox.httpserver.HttpStatus;
import se.ox.httpserver.Response;

import java.io.IOException;
import java.io.OutputStream;

public class HttpHelper {

    private HttpHelper() {
    }

    public static void sendError(OutputStream out, HttpStatus status) throws IOException {
        Response response = new Response(out);
        response.setStatus(status);
        response.send("{\"status\":\"failure\", \"Error\": \"" + status.getStatusCode() + " " + status.name() + "\" }");
    }
}
