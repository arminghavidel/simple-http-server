package se.ox;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket clientSocket) {
        try(OutputStream output = clientSocket.getOutputStream()) {
            StringBuilder response = new StringBuilder();
            String responseBody = "Hello, World!";
            addHeader(response, responseBody.length());
            response.append(responseBody);
            someDelay();
            output.write(response.toString().getBytes());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addHeader(StringBuilder sb, int contentLength) {
        sb.append("HTTP/1.1 200 OK\n")
                .append("Content-type: plain/text\n")
                .append("Content-length: ").append(contentLength)
                .append("\n")
                .append("\n");
    }

    private void someDelay(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
