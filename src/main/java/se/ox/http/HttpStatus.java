package se.ox.http;

public enum HttpStatus {

    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    NOT_FOUND(404),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500),
    ;

    private final int statusCode;

    HttpStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
