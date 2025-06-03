package se.ox.model;

public class User {

    private Integer id;
    private String username;

    public User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String username;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
