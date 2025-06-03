package se.ox.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ox.httpserver.Request;
import se.ox.httpserver.Response;
import se.ox.model.User;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserCrudHandler implements CrudHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserCrudHandler.class);

    private final Map<Integer, User> store = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);


    @Override
    public void create(Request request, Response response) throws IOException {
    }

    @Override
    public void read(Request request, Response response) throws IOException {

    }

    @Override
    public void update(Request request, Response response) throws IOException {

    }

    @Override
    public void delete(Request request, Response response) throws IOException {

    }
}
