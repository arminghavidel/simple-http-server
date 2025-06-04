package se.ox.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ox.handler.HttpHandler;
import se.ox.helper.HttpHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final Map<String, HttpHandler> handlers = new HashMap<>();

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        serverSocket = new ServerSocket(port);
        executorService = Executors.newVirtualThreadPerTaskExecutor();
        Thread serverThread = new Thread(() -> {
            logger.info("Server started on port {}", port);
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.submit(() -> handleConnection(clientSocket));
                } catch (IOException e) {
                    if (serverSocket.isClosed()) {
                        logger.info("Server socket closed, stopping server thread.");
                    } else {
                        logger.error("Error accepting connection", e);
                    }
                }
            }
        });
        serverThread.start();
    }

    public void stop() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public void addHandler(String path, HttpHandler handler) {
        handlers.put(path, handler);
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
            HttpHelper.sendError(out, HttpStatus.BAD_REQUEST);
            return;
        }

        String method = parts[0];
        String fullPath = parts[1];
        logger.info("Processing {} {}", method, fullPath);

        String path;
        Map<String, String> queryParams = new HashMap<>();
        int queryIndex = fullPath.indexOf('?');
        if (queryIndex >= 0) {
            path = fullPath.substring(0, queryIndex);
            String query = fullPath.substring(queryIndex + 1);
            queryParams = parseParams(query);
        } else {
            path = fullPath;
        }

        Map<String, String> headers = readHeaders(in);
        String body = readBody(in, headers);

        handle(method, path, headers, body, out, queryParams);
    }

    private void handle(String method, String path, Map<String, String> headers, String body, OutputStream out, Map<String, String> queryParams) throws IOException {
        HttpHandler handler = handlers.get(path);
        if (handler != null) {
            Request request = new Request(method, path, headers, body, queryParams);
            Response response = new Response(out);
            handler.handle(request, response);
        } else {
            logger.warn("No handler found for path: {}", path);
            HttpHelper.sendError(out, HttpStatus.NOT_FOUND);
        }
    }

    private static Map<String, String> readHeaders(BufferedReader in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    private static String readBody(BufferedReader in, Map<String, String> headers) throws IOException {
        String body = "";
        String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader != null) {
            int contentLength = Integer.parseInt(contentLengthHeader);
            char[] bodyChars = new char[contentLength];
            int read = in.read(bodyChars, 0, contentLength);
            body = new String(bodyChars, 0, read);
        }
        return body;
    }

    private Map<String, String> parseParams(String query) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }
}
