package se.ox.httpserver;

import java.io.IOException;

@FunctionalInterface
public interface HttpHandler {

    void handle(Request request, Response response) throws IOException;
}
