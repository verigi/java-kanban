package server.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseHttpHandler {

    protected void sendText(HttpExchange e, String message, int code) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        e.sendResponseHeaders(code, response.length);
        e.getResponseBody().write(response);
        e.close();
    }

    protected void sendNotFound(HttpExchange e, String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        e.sendResponseHeaders(404, response.length);
        e.getResponseBody().write(response);
        e.close();
    }

    protected void sendHasIntersections(HttpExchange e, String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        e.sendResponseHeaders(406, response.length);
        e.getResponseBody().write(response);
        e.close();
    }

    protected void sendHasIncorrectHttpDetails(HttpExchange e,String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        e.sendResponseHeaders(500, response.length);
        e.getResponseBody().write(response);
        e.close();
    }
}
