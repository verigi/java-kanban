package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.HttpTaskServer;
import server.adapters.DurationAdapter;
import server.serializers.EpicSerializer;
import server.adapters.LocalDateTimeAdapter;
import server.serializers.SubtaskSerializer;
import server.serializers.TaskSerializer;
import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import task.managers.service_manager.InMemoryTaskManager;
import task.managers.service_manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    //test url: "localhost:8080/storage/tasks"
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .registerTypeAdapter(Epic.class, new EpicSerializer())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        int id;
        // добавить все возможные пути обработки в список
        List<String> pathsList = new LinkedList<>();
        // TASKS
        pathsList.add("/storage/tasks"); // 0
        pathsList.add("/storage/tasks/\\d+"); // 1
        // SUBTASKS
        pathsList.add("/storage/subtasks"); // 2
        pathsList.add("/storage/subtasks/\\d+"); // 3
        // EPICS
        pathsList.add("/storage/epics"); // 4
        pathsList.add("/storage/epics/\\d+"); // 5
        pathsList.add("/storage/epics/\\d+/subtasks"); // 6
        // PRIORITIZED
        pathsList.add("/storage/prioritized"); // 7
        // HISTORY
        pathsList.add("/storage/history"); // 8


        // в зависимости от метода запроса вызвать нужный подобработчик
        switch (requestMethod) {
            case "GET" -> {
                // все обычные задания 0
                if (Pattern.matches(pathsList.get(0), path)) {
                    handleGetTasks(exchange, "tasks");
                }
                // задание по id 1
                if (Pattern.matches(pathsList.get(1), path)) {
                    id = Integer.parseInt(path.split("/")[3]);
                    handleGetTaskById(exchange, "task", id);
                }
                // все сабтаски 2
                if (Pattern.matches(pathsList.get(2), path)) {
                    handleGetTasks(exchange, "subtasks");
                }
                // сабтаск по id 3
                if (Pattern.matches(pathsList.get(3), path)) {
                    id = Integer.parseInt(path.split("/")[3]);
                    handleGetTaskById(exchange, "subtask", id);
                }
                // все эпики 4
                if (Pattern.matches(pathsList.get(4), path)) {
                    handleGetTasks(exchange, "epics");
                }
                // эпик по id 5
                if (Pattern.matches(pathsList.get(5), path)) {
                    id = Integer.parseInt(path.split("/")[3]);
                    handleGetTaskById(exchange, "epic", id);
                }
                // сабтаски эпика
                if (Pattern.matches(pathsList.get(6), path)) {
                    id = Integer.parseInt(path.split("/")[3]);
                    handleGetEpicSubtasks(exchange, id);
                }
                // приоритизированный
                if (Pattern.matches(pathsList.get(7), path)) {
                    handleGetTasks(exchange, "prioritized");
                }
                // история
                if (Pattern.matches(pathsList.get(8), path)) {
                    handleGetTasks(exchange, "history");
                }
            }
            case "POST" -> {
                // добавление задания
                if (Pattern.matches(pathsList.get(0), path)) {
                    handleAddTask(exchange, "task");
                }
                // обновление задания
                if (Pattern.matches(pathsList.get(1), path)) {
                    id = Integer.parseInt(path.split("/")[3]);
                    handleUpdateTask(exchange, "task", id);
                }
                // добавление подзадания
                if (Pattern.matches(pathsList.get(2), path)) {
                    handleAddTask(exchange, "subtask");
                }
                // обновление подзадания
                if (Pattern.matches(pathsList.get(3), path)) {
                    id = Integer.parseInt(path.split("/")[3]);
                    handleUpdateTask(exchange, "subtask", id);
                }
                // добавление эпика
                if (Pattern.matches(pathsList.get(4), path)) {
                    handleAddTask(exchange, "epic");
                }
            }
            case "DELETE" -> {
                // удаление задания по id
                if (Pattern.matches(pathsList.get(1), path)) {
                    id = Integer.parseInt(path.split("/")[3]);
                    handleDeleteTask(exchange, "task", id);
                }
                // удаление сабтаска по id
                if (Pattern.matches(pathsList.get(3), path)) {
                    id = Integer.parseInt(path.split("/")[3]);
                    handleDeleteTask(exchange, "subtask", id);
                }
                // удаление эпика по id
                if (Pattern.matches(pathsList.get(5), path)) {
                    id = Integer.parseInt(path.split("/")[3]);
                    handleDeleteTask(exchange, "epic", id);
                }
            }
            default -> System.out.println("Метод " + exchange.getRequestMethod() + " сейчас не поддерживается");
        }
    }


    private void handleGetTasks(HttpExchange exchange, String type) {
        System.out.println("Обработка запроса " + exchange.getRequestMethod() + ". Получение списка " + type);
        String notFoundText = "Не найдены задачи типа " + type;
        try {
            switch (type) {
                case "tasks" -> {
                    if (!manager.getAllTasks().isEmpty()) {
                        sendText(exchange, gson.toJson(manager.getAllTasks()), 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
                case "subtasks" -> {
                    if (!manager.getAllSubtasks().isEmpty()) {
                        sendText(exchange, gson.toJson(manager.getAllSubtasks()), 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
                case "epics" -> {
                    if (!manager.getAllEpicTasks().isEmpty()) {
                        sendText(exchange, gson.toJson(manager.getAllEpicTasks()), 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
                case "prioritized" -> {
                    if (!manager.getPrioritizedTasks().isEmpty()) {
                        sendText(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
                case "history" -> {
                    if (!manager.getHistory().isEmpty()) {
                        sendText(exchange, gson.toJson(manager.getHistory()), 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    private void handleGetTaskById(HttpExchange exchange, String type, int id) {
        System.out.println("Обработка запроса " + exchange.getRequestMethod() +
                ". Получение " + type + " по id: " + id);
        String notFoundText = "Заданиние типа " + type + " с id " + id + " не найдено";
        try {
            switch (type) {
                case "task" -> {
                    Optional<Task> task = manager.getAllTasks().stream().filter(x -> x.getId() == id).findAny();
                    if (task.isPresent()) {
                        sendText(exchange, gson.toJson(manager.getTask(id)), 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
                case "subtask" -> {
                    Optional<Subtask> subtask = manager.getAllSubtasks().stream().filter(x -> x.getId() == id).findAny();
                    if (subtask.isPresent()) {
                        sendText(exchange, gson.toJson(manager.getSubtask(id)), 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
                case "epic" -> {
                    Optional<Epic> epic = Optional.ofNullable(manager.getEpic(id));
                    if (epic.isPresent()) {
                        sendText(exchange, gson.toJson(manager.getEpic(id)), 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, int id) {
        System.out.println("Обработка запроса " + exchange.getRequestMethod() +
                ". Получение сабтасков эпика с id " + id);
        String notFoundText = "Эпик с id " + id + " не найден";
        try {
            Optional<Epic> epic = Optional.ofNullable(manager.getEpic(id));
            if (epic.isPresent()) {
                sendText(exchange, gson.toJson(manager.getCertainEpicSubtasks(epic.get().getId())), 200);
            } else {
                sendNotFound(exchange, notFoundText, 404);
            }
        } catch (IOException e) {
        }
    }

    private void handleAddTask(HttpExchange exchange, String type) {
        System.out.println("Обработка запроса " + exchange.getRequestMethod() + ". Добавление: " + type);
        try {
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Task task;
            Epic epic;
            Subtask subtask;
            switch (type) {
                case "task": {
                    task = gson.fromJson(body, Task.class);
                    manager.addTask(task);
                    sendText(exchange, "Создана задача " + task.getName() + ", id: " + task.getId(), 200);
                    break;
                }
                case "epic": {
                    epic = gson.fromJson(body, Epic.class);
                    manager.addEpic(epic);
                    sendText(exchange, "Создан эпик " + epic.getName() + ", id: " + epic.getId(), 200);
                    break;
                }
                case "subtask": {
                    subtask = gson.fromJson(body, Subtask.class);
                    manager.addSubtask(subtask);
                    sendText(exchange, "Создан сабтаск " + subtask.getName() + ", id: " + subtask.getId(), 200);
                    break;
                }
            }
        } catch (IOException | JsonSyntaxException e) {
        }
    }

    private void handleUpdateTask(HttpExchange exchange, String type, int id) {
        System.out.println("Обработка запроса " + exchange.getRequestMethod() + ". Обновление " + type);
        try {
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Task task;
            Epic epic;
            Subtask subtask;

            switch (type) {
                case "task": {
                    Optional<Task> target = manager.getAllTasks().stream().filter(x -> x.getId() == id).findAny();
                    if (target.isPresent()) {
                        task = gson.fromJson(body, Task.class);
                        manager.updateTask(task);
                        sendText(exchange, "Задание с id " + task.getId() + " обновлено", 201);
                    } else {
                        sendNotFound(exchange, "Задания с id " + id + " не найдено", 404);
                    }
                }
                case "epic": {
                    Optional<Epic> target = manager.getAllEpicTasks().stream().filter(x -> x.getId() == id).findAny();
                    if (target.isPresent()) {
                        epic = gson.fromJson(body, Epic.class);
                        manager.updateEpic(epic);
                        sendText(exchange, "Эпик с id " + epic.getId() + " обновлен", 201);
                    } else {
                        sendNotFound(exchange, "Эпика с id " + id + " не найдено", 404);
                    }
                }
                case "subtask": {
                    Optional<Subtask> target = manager.getAllSubtasks().stream().filter(x -> x.getId() == id).findAny();
                    if (target.isPresent()) {
                        subtask = gson.fromJson(body, Subtask.class);
                        manager.updateSubtask(subtask);
                        sendText(exchange, "Сабтаск в id " + subtask.getId() + " обновлен", 201);
                    } else {
                        sendNotFound(exchange, "Сабтаска с id " + id + " не найдено", 404);
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    private void handleDeleteTask(HttpExchange exchange, String type, int id) {
        System.out.println("Обработка запроса " + exchange.getRequestMethod() + ". Удаление задания типа " + type
                + " с id " + id);
        String successfulDeletion = "Задание типа " + type + " с id " + id + " удалено";
        String notFoundText = "Задание типа " + type + " с id " + id + " не найдено";
        try {
            switch (type) {
                case "task" -> {
                    Optional<Task> task = Optional.ofNullable(manager.getTask(id));
                    if (task.isPresent()) {
                        manager.deleteTaskByID(id);
                        sendText(exchange, successfulDeletion, 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
                case "subtask" -> {
                    Optional<Subtask> subtask = Optional.ofNullable(manager.getSubtask(id));
                    if (subtask.isPresent()) {
                        manager.deleteSubtaskByID(id);
                        sendText(exchange, successfulDeletion, 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
                case "epic" -> {
                    Optional<Epic> epic = Optional.ofNullable(manager.getEpic(id));
                    if (epic.isPresent()) {
                        manager.deleteEpicByID(id);
                        sendText(exchange, successfulDeletion, 200);
                    } else {
                        sendNotFound(exchange, notFoundText, 404);
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) {
        Gson gson1 = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("test", "test", LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofMinutes(10));
        Epic epic = new Epic("test", "test");
        Epic epic1 = new Epic("test", "test");
        Subtask subtask = new Subtask("test", "test", LocalDateTime.of(2000, 1, 1, 3, 0), Duration.ofMinutes(10), 2);
        Subtask subtask1 = new Subtask("test", "test", LocalDateTime.of(2000, 1, 1, 4, 0), Duration.ofMinutes(10), 2);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask1);

        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
            httpTaskServer.start();
        } catch (IOException e) {
        }
    }
}
