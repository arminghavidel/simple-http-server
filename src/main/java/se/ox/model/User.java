package se.ox.model;

public class User {

    private String id;
    private String username;

    public User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static class Builder {
        private String id;
        private String username;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }
    }
}
