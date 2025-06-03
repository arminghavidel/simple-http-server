package se.ox.handler;

import se.ox.httpserver.HttpStatus;
import se.ox.httpserver.Request;
import se.ox.httpserver.Response;

import java.io.IOException;

public interface CrudHandler extends HttpHandler {

    void create(Request request, Response response) throws IOException;

    void read(Request request, Response response) throws IOException;

    void update(Request request, Response response) throws IOException;

    void delete(Request request, Response response) throws IOException;

    @Override
    default void handle(Request request, Response response) throws IOException {
        switch (request.method()) {
            case "GET" -> read(request, response);
            case "POST" -> create(request, response);
            case "PUT" -> update(request, response);
            case "DELETE" -> delete(request, response);
            default -> response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }
}
