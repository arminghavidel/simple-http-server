package se.ox.handler;

import se.ox.http.Request;
import se.ox.http.Response;

import java.io.IOException;

@FunctionalInterface
public interface HttpHandler {

    void handle(Request request, Response response) throws IOException;
}
