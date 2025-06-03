package se.ox;

import org.slf4j.LoggerFactory;
import se.ox.handler.UserCrudHandler;
import se.ox.httpserver.HttpServer;
import se.ox.httpserver.HttpStatus;

import java.io.IOException;

public class Main {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        int port = getPort(args);
        HttpServer server = new HttpServer(port);

        server.addHandler("/", (request, response) -> {
            try {
                response.addHeader("Content-Type", "text/plain");
                response.send("Hello from multithreaded server!");
            } catch (IOException e) {
                logger.error("Error handling root request", e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });

        server.addHandler("/test", (request, response) -> {
            try {
                response.addHeader("Content-Type", "application/json");
                response.send("{\"status\":\"success\"}");
            } catch (IOException e) {
                logger.error("Error handling test request", e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });

        UserCrudHandler userHandler = new UserCrudHandler();
        server.addHandler("/users", (req, res) -> {
            try {
                userHandler.handle(req, res);
            } catch (IOException e) {
                logger.error("Error in userHandler", e);
                res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });

        server.start();
    }

    private static int getPort(String[] args) {
        int port = 8080;
        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid port number, using default 8080");
        }
        return port;
    }
}
