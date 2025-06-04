# Simple HTTP Server

This is a simple HTTP server project implemented in Java (JDK 21) using Maven. The project demonstrates basic HTTP request handling, multithreading, and the ability to register custom handlers for specific HTTP URL paths.

## Features

- **Multithreaded HTTP Server**: The server is capable of handling multiple HTTP requests concurrently using virtual threads.
- **Dynamic Handler Registration**: Allows adding custom HTTP handlers for different URL paths.
- **Java 21 Implementation**: Uses only JDK 21 libraries (except for logging or tests).

## Project Structure

The project follows a standard Maven layout and includes the following key directories:

- `src/main/java/se/ox/`: Contains the core Java source code.
    - `HttpServer.java`: Main class responsible for handling HTTP requests, multithreading, and handler registration.
    - `HttpHandler.java`: Interface for defining custom HTTP request handlers.
    - `HttpStatus.java`: Enum for HTTP status codes.
    - `Request.java`, `Response.java`: Classes for handling HTTP requests and responses.
- `src/test/java/se/ox/`: Contains unit tests for the project.
- `pom.xml`: Maven build configuration file.

## Requirements

- JDK 21 or later
- Maven for building the project
- Logging library (SLF4J) for logging

## How to Build and Run

### 1. Clone the Repository
If you haven't already, clone the repository to your local machine:

```bash
git clone https://github.com/arminghavidel/simple-http-server.git
cd http-server
```

### 2. Build the Project
Use Maven to build the project:

```bash
mvn clean package
```

### 3. Run the HTTP Server
Once the project is built, you can run the Main.java class to start the HTTP server. The server will listen on a specified port (default is 8080).

```bash
java -jar.\target\http-server-1.0-SNAPSHOT.jar 
```
### 4. Register Custom Handlers
You can register custom handlers for specific URL paths by using the addHandler method in HttpServer.java. For example:

````java
HttpServer server = new HttpServer(8080);
server.addHandler("/my-path", (request, response) -> {
    response.setBody("Custom handler response");
    response.setStatus(HttpStatus.OK);
});
````


## Notes
The server is designed to be thread-safe and can handle multiple requests concurrently.

The handler registration allows mapping specific URL paths to handler functions for custom behavior.

The project uses virtual threads for efficient request handling, leveraging JDK 21 features.