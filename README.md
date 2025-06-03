# Simple Java HTTP Server

This project is a lightweight HTTP server written in Java 21. It was created for a technical interview and demonstrates core server functionality, multithreading, and handler registration.

## Features

- Basic HTTP server with multithreading
- Ability to register custom handlers for specific HTTP routes
- Built with JDK 21 and Maven
- No external dependencies (except for logging/testing)

## How to Run

1. Make sure you have **Java 21** and **Maven** installed.
2. Clone or unzip the project.
3. In the root folder, build and run using Maven:

```bash
mvn clean package
java -jar target/http-server-1.0-SNAPSHOT.jar 8080
```

Notes
This server is designed to be as minimal as possible.

Please see comments in the code for assumptions and edge case handling.

Only JDK 21 standard libraries are used.

---