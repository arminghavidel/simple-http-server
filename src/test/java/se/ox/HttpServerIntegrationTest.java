package se.ox;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import se.ox.handler.UserCrudHandler;
import se.ox.helper.JsonHelper;
import se.ox.http.HttpServer;
import se.ox.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpServerIntegrationTest {

    private HttpServer server;
    private static final int PORT = 8080;
    private HttpClient client;

    @BeforeAll
    public void startServer() throws IOException {
        server = new HttpServer(PORT);

        UserCrudHandler userHandler = new UserCrudHandler();
        server.addHandler("/users", userHandler);

        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    public void stopServer() throws IOException {
        server.stop();
    }

    @Test
    void testCreateUser() throws IOException, InterruptedException {
        HttpResponse<String> response = createUser();

        assertEquals(201, response.statusCode(), "Expected HTTP 201 Created");
        assertTrue(response.body().contains("testUser"), "Response should contain created username");
    }

    @Test
    void testGetUser() throws IOException, InterruptedException {
        HttpResponse<String> user = createUser();
        Map<String, String> userJson = JsonHelper.readJson(user.body());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/users?id=" + JsonHelper.getString(userJson, "id")))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Expected HTTP 200 OK");
        assertTrue(response.body().contains("testUser"), "Response should contain username");
    }

    @Test
    void testUpdateUser() throws IOException, InterruptedException {
        HttpResponse<String> user = createUser();
        Map<String, String> userJson = JsonHelper.readJson(user.body());
        String updateJson = "{\"username\":\"updatedTestUser\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/users?id=" + JsonHelper.getString(userJson, "id")))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Expected HTTP 200 OK");
        assertTrue(response.body().contains("updatedTestUser"), "Response should reflect updated username");
    }

    @Test
    void testDeleteUser() throws IOException, InterruptedException {
        HttpResponse<String> user = createUser();
        Map<String, String> userJson = JsonHelper.readJson(user.body());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/users?id=" + JsonHelper.getString(userJson, "id")))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.NO_CONTENT.getStatusCode(), response.statusCode(), "Expected HTTP 204 No Content");
    }

    private HttpResponse<String> createUser() throws IOException, InterruptedException {
        String userJson = "{\"username\":\"testUser\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
