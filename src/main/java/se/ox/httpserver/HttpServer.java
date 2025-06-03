package se.ox.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ox.handler.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final int port;
    private final Map<String, HttpHandler> handlers = new HashMap<>();

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() {

        try (
                ServerSocket serverSocket = new ServerSocket(port);
                ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            logger.info("Server started on port {}", port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleConnection(clientSocket));
            }
        } catch (IOException e) {
            logger.error("Server startup failed on port {}", port, e);
        }
    }

    private void handleConnection(Socket clientSocket) {
        try (
                clientSocket;
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            processRequest(in, out);
        } catch (IOException e) {
            logger.error("Error handling connection: {}", e.getMessage(), e);
        }
    }

    private void processRequest(BufferedReader in, OutputStream out) throws IOException {

        String requestLine = in.readLine();
        if (requestLine == null)
            return;

        logger.info("Received request: {}", requestLine);
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            logger.warn("Malformed request: {}", requestLine);
            sendError(out, HttpStatus.BAD_REQUEST);
            return;
        }

        String method = parts[0];
        String path = parts[1];
        logger.info("Processing {} {}", method, path);

        handlePath(method, path, out);
    }

    private void handlePath(String method, String path, OutputStream out) throws IOException {
        HttpHandler handler = handlers.get(path);
        if (handler != null) {
            Request request = new Request(method, path);
            Response response = new Response(out);
            handler.handle(request, response);
        } else {
            logger.warn("No handler found for path: {}", path);
            sendError(out, HttpStatus.NOT_FOUND);
        }
    }

    private void sendError(OutputStream out, HttpStatus status) throws IOException {
        Response response = new Response(out);
        response.setStatus(status);
        response.send("Error: " + status.getStatusCode() + " " + status.name());
    }

    public void addHandler(String path, HttpHandler handler) {
        handlers.put(path, handler);
    }
}
