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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpServerIntegrationTest {

    private HttpServer server;
    private HttpClient client;
    private static final int PORT = 8080;
    private ExecutorService executorService;


    @BeforeAll
    public void startServer() throws IOException {
        server = new HttpServer(PORT);

        UserCrudHandler userHandler = new UserCrudHandler();
        server.addHandler("/users", userHandler);

        server.start();
        client = HttpClient.newHttpClient();
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @AfterAll
    public void stopServer() throws IOException {
        server.stop();
        executorService.shutdown();
    }

    @Test
    void testCreateUser() throws IOException, InterruptedException {
        String username = "user-" + UUID.randomUUID();
        HttpResponse<String> response = createUser(username);

        assertEquals(HttpStatus.CREATED.getStatusCode(), response.statusCode(), "Expected HTTP 201 Created");
        assertTrue(response.body().contains(username), "Response should contain created username");
    }

    @Test
    void testGetUser() throws IOException, InterruptedException {
        String username = "user-" + UUID.randomUUID();
        HttpResponse<String> user = createUser(username);
        Map<String, String> userJson = JsonHelper.readJson(user.body());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/users?id=" + JsonHelper.getString(userJson, "id")))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.getStatusCode(), response.statusCode(), "Expected HTTP 200 OK");
        assertTrue(response.body().contains(username), "Response should contain username");
    }

    @Test
    void testUpdateUser() throws IOException, InterruptedException {
        String username = "user-" + UUID.randomUUID();
        HttpResponse<String> user = createUser(username);
        Map<String, String> userJson = JsonHelper.readJson(user.body());
        String updateJson = "{\"username\":\"updatedTestUser\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/users?id=" + JsonHelper.getString(userJson, "id")))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.getStatusCode(), response.statusCode(), "Expected HTTP 200 OK");
        assertTrue(response.body().contains("updatedTestUser"), "Response should reflect updated username");
    }

    @Test
    void testDeleteUser() throws IOException, InterruptedException {
        String username = "user-" + UUID.randomUUID();
        HttpResponse<String> user = createUser(username);
        Map<String, String> userJson = JsonHelper.readJson(user.body());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/users?id=" + JsonHelper.getString(userJson, "id")))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.NO_CONTENT.getStatusCode(), response.statusCode(), "Expected HTTP 204 No Content");
    }

    private HttpResponse<String> createUser(String username) throws IOException, InterruptedException {
        String userJson = "{\"username\":\""+ username +"\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void testConcurrentUserCreations() {
        int numThreads = 10;

        CompletableFuture<?>[] futures = IntStream.range(0, numThreads)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        String username = "user-" + UUID.randomUUID();
                        HttpResponse<String> response = createUser(username);
                        assertEquals(HttpStatus.CREATED.getStatusCode(), response.statusCode());
                    } catch (Exception e) {
                        throw new AssertionError("Thread failed", e);
                    }
                }, executorService))
                .toArray(CompletableFuture[]::new);

        assertDoesNotThrow(() -> CompletableFuture.allOf(futures).get(10, TimeUnit.SECONDS));
    }
}
