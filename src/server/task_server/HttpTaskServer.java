package server.task_server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import enums.Endpoint;
import enums.Status;
import enums.Type;
import exceptions.IncorrectPathException;
import exceptions.TaskDetailsFormatException;
import exceptions.TaskTypeException;
import server.handlers.ResponseHttpHandler;
import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import task.managers.Managers;
import task.managers.service_manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/*
Ирек, привет! Спасибо тебе за долгое ожидание работы.
Сразу "подсвечу" несколько моментов:
1. Подготовил работу сервера можно сказать единым монолитом - реализация обработчиков эндпоинтов выполнил вложенным
классом. Не знаю, насколько верным было это решение, так что буду рад рекомендациям по этому направлению.
2. Из возможных вариантов, где вероятно стоит подправить - большое кол-во кастомных эксепшенов.
Насколько это целесообразно? Можно сказать, что варианты ошибок, которые возникают в работе приложения, выделены в
отдельные классы исключений.
3. С десяток раз перечитал ТЗ и посмотрел QA в записи (видимо, одного из предыдущих потоков): как я понял,
ТЗ поменялось, т.к. в той когорте, чей QA смотрел, есть задания по реализации еще и KVServer`а.
Ну или я просто не вижу пункта по этому моменту в ТЗ :D
4. По поводу тестов: если нетрудно, посмотри, пожалуйста, их реализацию. Насколько она верна и что можно/нужно
поправить?

Буду рад любым советам и рекомендациям. Уверен, что больше так не пропаду.
Заранее благодарю за ответ.

С уважением, Иван
 */
public class HttpTaskServer {
    private final int port = 8080;
    private HttpServer server;
    private TaskManager manager;
    private final Gson gson = Managers.createGson();

    public HttpTaskServer(TaskManager manager) {
        try {
            this.manager = manager;
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/storage", new ControlHttpHandler());
        } catch (IOException exception) {
            System.out.println("Ошибка запуска сервера");
            exception.printStackTrace();
        }
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен. Номер порта: " + port);
    }

    public void stop() {
        server.stop(5);
        System.out.println("Остановка сервера");
    }

    public TaskManager getManager() {
        return manager;
    }

    class ControlHttpHandler extends ResponseHttpHandler implements HttpHandler {
        TaskHandler taskHandler = new TaskHandler();


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = identifyEndpoint(method, path);
            switch (endpoint) {
                // получение списков
                case GET_ALL_TASKS -> taskHandler.getList(exchange, Type.TASK);
                case GET_ALL_SUBTASKS -> taskHandler.getList(exchange, Type.SUBTASK);
                case GET_ALL_EPICS -> taskHandler.getList(exchange, Type.EPIC);
                // получение по id
                case GET_TASK_BY_ID -> taskHandler.getById(exchange, Type.TASK, extractId(path));
                case GET_SUBTASK_BY_ID -> taskHandler.getById(exchange, Type.SUBTASK, extractId(path));
                case GET_EPIC_BY_ID -> taskHandler.getById(exchange, Type.EPIC, extractId(path));
                // получение сабтасков эпика
                case GET_CERTAIN_EPIC_SUBTASKS -> taskHandler.getEpicSubtasks(exchange, extractId(path));
                // получение приоритизированного списка
                case GET_PRIORITIZED -> taskHandler.getPrioritized(exchange);
                // получение истории
                case GET_HISTORY -> taskHandler.getHistory(exchange);
                // добавление заданий
                case ADD_TASK -> taskHandler.addAnyTask(exchange, Type.TASK);
                case ADD_SUBTASK -> taskHandler.addAnyTask(exchange, Type.SUBTASK);
                case ADD_EPIC -> taskHandler.addAnyTask(exchange, Type.EPIC);
                // обновление заданий
                case UPDATE_TASK -> taskHandler.updateAnyTask(exchange, Type.TASK);
                case UPDATE_SUBTASK -> taskHandler.updateAnyTask(exchange, Type.SUBTASK);
                case UPDATE_EPIC -> taskHandler.updateAnyTask(exchange, Type.EPIC);
                // удаление списков заданий
                case DELETE_ALL_TASKS -> taskHandler.deleteTaskList(exchange, Type.TASK);
                case DELETE_ALL_SUBTASKS -> taskHandler.deleteTaskList(exchange, Type.SUBTASK);
                case DELETE_ALL_EPICS -> taskHandler.deleteTaskList(exchange, Type.EPIC);
                // удаление задания по id
                case DELETE_TASK_BY_ID -> taskHandler.deleteById(exchange, Type.TASK, extractId(path));
                case DELETE_SUBTASK_BY_ID -> taskHandler.deleteById(exchange, Type.SUBTASK, extractId(path));
                case DELETE_EPIC_BY_ID -> taskHandler.deleteById(exchange, Type.EPIC, extractId(path));
                // ошибочные запросы
                case UNKNOWN_METHOD -> sendHasIncorrectHttpDetails(exchange, "Некорретный метод запроса");
            }
        }

        private Endpoint identifyEndpoint(String method, String path) {
            ArrayList<String> paths = new ArrayList<>();
            paths.add("/storage/tasks");
            paths.add("^/storage/tasks/\\d+$");
            paths.add("/storage/subtasks");
            paths.add("/storage/subtasks/\\d+$");
            paths.add("/storage/epics");
            paths.add("/storage/epics/\\d+");
            paths.add("/storage/epics/\\d+/subtasks");
            paths.add("/storage/prioritized");
            paths.add("/storage/history");

            switch (method) {
                case "GET" -> {
                    if (Pattern.matches(paths.get(0), path)) return Endpoint.GET_ALL_TASKS;
                    if (Pattern.matches(paths.get(1), path)) return Endpoint.GET_TASK_BY_ID;
                    if (Pattern.matches(paths.get(2), path)) return Endpoint.GET_ALL_SUBTASKS;
                    if (Pattern.matches(paths.get(3), path)) return Endpoint.GET_SUBTASK_BY_ID;
                    if (Pattern.matches(paths.get(4), path)) return Endpoint.GET_ALL_EPICS;
                    if (Pattern.matches(paths.get(5), path)) return Endpoint.GET_EPIC_BY_ID;
                    if (Pattern.matches(paths.get(6), path)) return Endpoint.GET_CERTAIN_EPIC_SUBTASKS;
                    if (Pattern.matches(paths.get(7), path)) return Endpoint.GET_PRIORITIZED;
                    if (Pattern.matches(paths.get(8), path)) return Endpoint.GET_HISTORY;
                }
                case "POST" -> {
                    if (Pattern.matches(paths.get(0), path)) return Endpoint.ADD_TASK;
                    if (Pattern.matches(paths.get(1), path)) return Endpoint.UPDATE_TASK;
                    if (Pattern.matches(paths.get(2), path)) return Endpoint.ADD_SUBTASK;
                    if (Pattern.matches(paths.get(3), path)) return Endpoint.UPDATE_SUBTASK;
                    if (Pattern.matches(paths.get(4), path)) return Endpoint.ADD_EPIC;
                    if (Pattern.matches(paths.get(5), path)) return Endpoint.UPDATE_EPIC;
                }
                case "DELETE" -> {
                    if (Pattern.matches(paths.get(0), path)) return Endpoint.DELETE_ALL_TASKS;
                    if (Pattern.matches(paths.get(1), path)) return Endpoint.DELETE_TASK_BY_ID;
                    if (Pattern.matches(paths.get(2), path)) return Endpoint.DELETE_ALL_SUBTASKS;
                    if (Pattern.matches(paths.get(3), path)) return Endpoint.DELETE_SUBTASK_BY_ID;
                    if (Pattern.matches(paths.get(4), path)) return Endpoint.DELETE_ALL_EPICS;
                    if (Pattern.matches(paths.get(5), path)) return Endpoint.DELETE_EPIC_BY_ID;
                }
                default -> {
                    return Endpoint.UNKNOWN_METHOD;
                }
            }
            throw new IncorrectPathException("Некорректный адрес запроса");
        }

        private int extractId(String path) {
            String[] pathParts = path.split("/");
            try {
                return Integer.parseInt(pathParts[3]);
            } catch (NumberFormatException e) {
                throw new IncorrectPathException("ID не является числовым значением");
            }
        }

        class TaskHandler {
            private void getList(HttpExchange exchange, Type type) throws IOException {
                String notFoundText = "Список задач типа " + type + " пуст";
                System.out.println("Обработка метода " + exchange.getRequestMethod() +
                        ". Получение задач типа " + type);

                switch (type) {
                    case TASK -> {
                        if (manager.getAllTasks().isEmpty()) sendNotFound(exchange, notFoundText);
                        sendText(exchange, gson.toJson(manager.getAllTasks()), 200);
                    }
                    case SUBTASK -> {
                        if (manager.getAllSubtasks().isEmpty()) sendNotFound(exchange, notFoundText);
                        sendText(exchange, gson.toJson(manager.getAllSubtasks()), 200);
                    }
                    case EPIC -> {
                        if (manager.getAllEpicTasks().isEmpty()) sendNotFound(exchange, notFoundText);
                        sendText(exchange, gson.toJson(manager.getAllEpicTasks()), 200);
                    }
                    default -> throw new TaskTypeException("Некорректный тип задания");
                }
            }

            private void getById(HttpExchange exchange, Type type, int id) throws IOException {
                System.out.println("Обработка запроса " + exchange.getRequestMethod() +
                        ". Получение " + type + " с id " + id);

                try {
                    switch (type) {
                        case TASK -> {
                            manager.getTask(id);
                            sendText(exchange, gson.toJson(manager.getTask(id)), 200);
                        }
                        case SUBTASK -> {
                            manager.getSubtask(id);
                            sendText(exchange, gson.toJson(manager.getSubtask(id)), 200);
                        }
                        case EPIC -> {
                            manager.getEpic(id);
                            sendText(exchange, gson.toJson(manager.getEpic(id)), 200);
                        }
                        default -> throw new TaskTypeException("Некорректный тип задания");
                    }
                } catch (NoSuchElementException exception) {
                    sendNotFound(exchange, exception.getMessage());
                }
            }

            private void getEpicSubtasks(HttpExchange exchange, int id) throws IOException {
                System.out.println("Обработка запроса " + exchange.getRequestMethod() +
                        ". Получение сабтасков эпика с id " + id);
                try {
                    sendText(exchange, gson.toJson(manager.getCertainEpicSubtasks(id)), 200);
                } catch (NoSuchElementException exception) {
                    sendNotFound(exchange, exception.getMessage());
                }
            }

            private void getPrioritized(HttpExchange exchange) throws IOException {
                String notFoundText = "Приоритезированный список пуст";
                System.out.println("Обработка запроса " + exchange.getRequestMethod() +
                        ". Получение приоритезированного списка");

                if (manager.getPrioritizedTasks().isEmpty()) sendNotFound(exchange, notFoundText);
                sendText(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
            }

            private void getHistory(HttpExchange exchange) throws IOException {
                String notFoundText = "История пуста";
                System.out.println("Обработка запроса " + exchange.getRequestMethod() +
                        ". Получение истории менеджера");

                if (manager.getHistory().isEmpty()) sendNotFound(exchange, notFoundText);
                sendText(exchange, gson.toJson(manager.getHistory()), 200);
            }

            private void addAnyTask(HttpExchange exchange, Type type) throws IOException {
                String successfulAdding = type + " успешно добавлен(о) с id ";

                System.out.println("Обработка запроса " + exchange.getRequestMethod() +
                        ". Добавление задания типа " + type);
                Task task;
                Epic epic;
                Subtask subtask;

                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                try {
                    switch (type) {
                        case TASK -> {
                            task = gson.fromJson(body, Task.class);
                            if (task.getStatus() == null) task.setStatus(Status.NEW);
                            if (task.getType() == null) task.setType(Type.TASK);
                            manager.addTask(task);
                            sendText(exchange, successfulAdding + task.getId(), 201);
                        }
                        case EPIC -> {
                            epic = gson.fromJson(body, Epic.class);
                            if (epic.getStatus() == null) epic.setStatus(Status.NEW);
                            if (epic.getType() == null) epic.setType(Type.EPIC);
                            manager.addEpic(epic);
                            sendText(exchange, successfulAdding + epic.getId(), 201);
                        }
                        case SUBTASK -> {
                            subtask = gson.fromJson(body, Subtask.class);
                            if (subtask.getStatus() == null) subtask.setStatus(Status.NEW);
                            if (subtask.getType() == null) subtask.setType(Type.SUBTASK);
                            manager.addSubtask(subtask);
                            sendText(exchange, successfulAdding + subtask.getId(), 201);
                        }
                        default -> throw new TaskTypeException("Некорретный тип задания");
                    }
                } catch (TaskDetailsFormatException exception) {
                    sendHasIntersections(exchange, exception.getMessage());
                }
            }

            private void updateAnyTask(HttpExchange exchange, Type type) throws IOException {
                System.out.println("Обработка запроса " + exchange.getRequestMethod() +
                        ". Обновление задания типа " + type);
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                Epic epic = gson.fromJson(body, Epic.class);

                try {
                    switch (type) {
                        case TASK -> {
                            manager.updateTask(task);
                            sendText(exchange, "Задание с id " + task.getId() + " обновлено", 201);
                        }
                        case SUBTASK -> {
                            manager.updateSubtask(subtask);
                            sendText(exchange, "Сабтаск с id " + subtask.getId() + " обновлен", 201);
                        }
                        case EPIC -> {
                            manager.updateEpic(epic);
                            sendText(exchange, "Эпик с id " + epic.getId() + " обновлен", 201);
                        }
                    }
                } catch (NoSuchElementException exception) {
                    sendNotFound(exchange, exception.getMessage());
                }
            }

            private void deleteTaskList(HttpExchange exchange, Type type) throws IOException {
                String successfulDeleting = "Список заданий типа " + type + " успешно очищен";
                System.out.println("Обработка метода " + exchange.getRequestMethod() +
                        ". Удаление списков заданий типа " + type);

                switch (type) {
                    case TASK -> {
                        manager.clearAllTasks();
                        sendText(exchange, successfulDeleting, 200);
                    }
                    case SUBTASK -> {
                        manager.clearAllSubtasks();
                        sendText(exchange, successfulDeleting, 200);
                    }
                    case EPIC -> {
                        manager.clearAllEpics();
                        sendText(exchange, successfulDeleting, 200);
                    }
                    default -> throw new TaskTypeException("Некорретный тип задания");
                }
            }

            private void deleteById(HttpExchange exchange, Type type, int id) throws IOException {
                String successfulDeleting = "Задание типа " + type + " с id " + id + " успешно удалено";
                System.out.println("Обработка метода " + exchange.getRequestMethod() +
                        type + " с id: " + id);

                try {
                    switch (type) {
                        case TASK -> {
                            manager.deleteTaskByID(id);
                            sendText(exchange, successfulDeleting, 200);
                        }
                        case SUBTASK -> {
                            manager.deleteSubtaskByID(id);
                            sendText(exchange, successfulDeleting, 200);
                        }
                        case EPIC -> {
                            manager.deleteEpicByID(id);
                            sendText(exchange, successfulDeleting, 200);
                        }
                    }
                } catch (NoSuchElementException exception) {
                    sendNotFound(exchange, exception.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer(Managers.getDefault());
        taskServer.start();
    }
}