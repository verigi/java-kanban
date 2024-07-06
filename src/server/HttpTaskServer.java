package server;

import com.sun.net.httpserver.HttpServer;
import server.handlers.TaskHandler;
import task.managers.Managers;
import task.managers.service_manager.TaskManager;


import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer{
    private final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/storage", new TaskHandler(manager));
    }

    public void start() {
        System.out.println("Запуск сервера. Номер порта: " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("Остановка сервера");
        server.stop(2);
    }

    //test url: "localhost:8080/tasks/tasks"

}
