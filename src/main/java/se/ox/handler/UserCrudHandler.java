package se.ox.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ox.helper.HttpHelper;
import se.ox.helper.JsonHelper;
import se.ox.http.HttpStatus;
import se.ox.http.Request;
import se.ox.http.Response;
import se.ox.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserCrudHandler implements CrudHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserCrudHandler.class);

    private final Map<Integer, User> store = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);


    @Override
    public void create(Request request, Response response) throws IOException {

        Map<String, String> body = JsonHelper.readJson(request.body());
        var user = User.builder()
                .id(nextId.getAndIncrement())
                .username(JsonHelper.getString(body, "username"))
                .build();
        store.put(user.getId(), user);
        logger.info("User {} created", user.getId());

        response.setStatus(HttpStatus.CREATED);
        response.send("{\"status\":\"success\", \"id\": " + user.getId() + ", \"username\": \"" + user.getUsername() + "\" }");
    }

    @Override
    public void read(Request request, Response response) throws IOException {

        var user = getUser(request, response);
        if (user == null) return;

        logger.info("User {} fetched", user.getId());
        Map<String, String> userMap = new HashMap<>();
        userMap.put("id", user.getId().toString());
        userMap.put("username", user.getUsername());

        response.setStatus(HttpStatus.OK);
        response.send(JsonHelper.toJson(userMap));
    }

    @Override
    public void update(Request request, Response response) throws IOException {

        var user = getUser(request, response);
        if (user == null) return;

        Map<String, String> body = JsonHelper.readJson(request.body());
        user.setUsername(JsonHelper.getString(body, "username"));

        logger.info("User {} updated", user.getId());

        response.setStatus(HttpStatus.OK);
        response.send("{\"status\":\"success\", \"id\": " + user.getId() + ", \"username\": \"" + user.getUsername() + "\" }");
    }

    @Override
    public void delete(Request request, Response response) throws IOException {

        Integer id = JsonHelper.getInt(request.queryParams(), "id");
        store.remove(id);

        logger.info("User {} removed", id);

        response.setStatus(HttpStatus.NO_CONTENT);
        response.send("");
    }

    private User getUser(Request request, Response response) throws IOException {
        Integer id = JsonHelper.getInt(request.queryParams(), "id");
        var user = store.get(id);

        if (user == null) {
            logger.info("User {} not found", id);
            HttpHelper.sendError(response.getOutput(), HttpStatus.NOT_FOUND);
            return null;
        }
        return user;
    }
}
