package se.ox.handler;

import se.ox.httpserver.Request;
import se.ox.httpserver.Response;

import java.io.IOException;

@FunctionalInterface
public interface HttpHandler {

    void handle(Request request, Response response) throws IOException;
}
