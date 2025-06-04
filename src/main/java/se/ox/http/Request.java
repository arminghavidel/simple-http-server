package se.ox.http;

import java.util.Map;

public record Request(
        String method,
        String path,
        Map<String, String> headers,
        String body,
        Map<String, String> queryParams
) {
}
