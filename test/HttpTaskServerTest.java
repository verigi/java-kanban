import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import server.task_server.HttpTaskServer;
import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import task.managers.service_manager.InMemoryTaskManager;
import task.managers.service_manager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpTaskServerTest {
    private Task task_1;
    private Task task_2;
    private Epic epic_1;
    private Subtask subtask_1;
    private Subtask subtask_2;
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final String tasksURL = "http://localhost:8080/storage/tasks";
    private final String subtasksURL = "http://localhost:8080/storage/subtasks";
    private final String epicsURL = "http://localhost:8080/storage/epics";
    private final String historyURL = "http://localhost:8080/storage/history";
    private final String prioritizedURL = "http://localhost:8080/storage/prioritized";
    private final String incorrectURL = "http://localhost:8080/storage/someurl";
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void setUp() {
        manager.clearAllTasks();
        manager.clearAllSubtasks();
        manager.clearAllEpics();
        initializeTasks();
        addTasksToManager();
        server.start();
    }

    @AfterEach
    public void stop() {
        server.stop();
    }

    @Test
    @DisplayName("Получение всех заданий/сабтасков/эпиков")
    public void shouldReturnListsOfTasksSubsEpics() throws IOException, InterruptedException {
        assertEquals(200, sendGetRequest(tasksURL).statusCode());
        assertEquals(200, sendGetRequest(subtasksURL).statusCode());
        assertEquals(200, sendGetRequest(epicsURL).statusCode());
    }

    @Test
    @DisplayName("Получение задания/сабтаска/эпика по id")
    public void shouldReturnTaskSubEpicById() throws IOException, InterruptedException {
        Task comparisonTask = gson.fromJson(sendGetRequest(tasksURL + "/1").body().toString(), Task.class);
        Subtask comparisonSubtask = gson.fromJson(sendGetRequest(subtasksURL + "/4").body().toString(),
                Subtask.class);
        Epic comparisonEpic = gson.fromJson(sendGetRequest(epicsURL + "/3").body().toString(), Epic.class);

        assertEquals(200, sendGetRequest(tasksURL + "/1").statusCode());
        assertEquals(200, sendGetRequest(subtasksURL + "/4").statusCode());
        assertEquals(200, sendGetRequest(epicsURL + "/3").statusCode());
        assertEquals(task_1, comparisonTask);
        assertEquals(subtask_1, comparisonSubtask);
        assertEquals(epic_1, comparisonEpic);
    }

    @Test
    @DisplayName("Получение по несуществующему id")
    public void shouldReturn404CodeForSearchById() throws IOException, InterruptedException {
        assertEquals(404, sendGetRequest(tasksURL + "/10").statusCode());
        assertEquals(404, sendGetRequest(subtasksURL + "/10").statusCode());
        assertEquals(404, sendGetRequest(epicsURL + "/10").statusCode());
    }

    @Test
    @DisplayName("Получение пустых списков заданий/подзаданий/эпиков")
    public void shouldReturnEmptyListsOfTasksSubsEpics() throws IOException, InterruptedException {
        manager.clearAllTasks();
        manager.clearAllSubtasks();
        manager.clearAllEpics();
        assertEquals(404, sendGetRequest(tasksURL).statusCode());
        assertEquals(404, sendGetRequest(subtasksURL).statusCode());
        assertEquals(404, sendGetRequest(epicsURL).statusCode());
    }

    @Test
    @DisplayName("Получение истории менеджера")
    public void shouldReturnHistoryOfTheManager() throws IOException, InterruptedException {
        sendGetRequest(tasksURL + "/1");
        sendGetRequest(tasksURL + "/2");
        assertEquals(200, sendGetRequest(historyURL).statusCode());
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    @DisplayName("Получение пустой истории менеджера")
    public void shouldReturnCodeOfEmptyHistoryOfTheManager() throws IOException, InterruptedException {
        assertEquals(404, sendGetRequest(historyURL).statusCode());
    }

    @Test
    @DisplayName("Получение приоритизированного списка")
    public void shouldReturnPrioritizedList() throws IOException, InterruptedException {
        assertEquals(200, sendGetRequest(prioritizedURL).statusCode());
    }

    @Test
    @DisplayName("Получение пустого приоритизированного списка")
    public void shouldReturn404CodeForPrioritizedList() throws IOException, InterruptedException {
        manager.clearAllTasks();
        manager.clearAllSubtasks();
        assertEquals(404, sendGetRequest(prioritizedURL).statusCode());
    }

    @Test
    @DisplayName("Добавление задания")
    public void shouldAddTaskToManager() throws IOException, InterruptedException {
        Task task = new Task("Задание_добавление", "Описание",
                LocalDateTime.of(2000, 1, 1, 3, 0), Duration.ofMinutes(10));
        assertEquals(201, sendPostRequest(tasksURL, task).statusCode());
    }

    @Test
    @DisplayName("Добавление эпика")
    public void shouldAddEpicToManager() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик_добавление", "Описание");
        assertEquals(201, sendPostRequest(epicsURL, epic).statusCode());
    }

    @Test
    @DisplayName("Добавление сабтаска")
    public void shouldAddSubtaskToManager() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Сабтаск_добавление", "Описание", Status.NEW,
                LocalDateTime.of(2001, 1, 1, 0, 0), Duration.ofMinutes(10), 3);
        assertEquals(201, sendPostRequest(subtasksURL, subtask).statusCode());
    }

    @Test
    @DisplayName("Обновление задания")
    public void shouldUpdateTask() throws IOException, InterruptedException {
        task_1.setName("Задание_обновление");
        assertEquals(201, sendPostRequest(tasksURL + "/1", task_1).statusCode());
        assertEquals("Задание_обновление", manager.getTask(1).getName());
    }

    @Test
    @DisplayName("Обновление сабтаска")
    public void shouldUpdateSubtask() throws IOException, InterruptedException {
        subtask_1.setName("Сабтаск_обновление");
        assertEquals(201, sendPostRequest(subtasksURL + "/4", subtask_1).statusCode());
        assertEquals("Сабтаск_обновление", manager.getSubtask(4).getName());
    }

    @Test
    @DisplayName("Обновление эпика")
    public void shouldUpdateEpic() throws IOException, InterruptedException {
        epic_1.setName("Эпик_обновление");
        assertEquals(201, sendPostRequest(epicsURL + "/3", epic_1).statusCode());
        assertEquals("Эпик_обновление", manager.getEpic(3).getName());
    }

    @Test
    @DisplayName("Удаление задания")
    public void shouldDeleteTask() throws IOException, InterruptedException {
        assertEquals(200, sendDeleteRequest(tasksURL + "/1").statusCode());
        assertEquals(List.of(task_2), manager.getAllTasks());
    }

    @Test
    @DisplayName("Удаление сабтаска")
    public void shouldAddSubtaskAndDelete() throws IOException, InterruptedException {
        assertEquals(200, sendDeleteRequest(subtasksURL + "/4").statusCode());
        assertEquals(List.of(subtask_2), manager.getAllSubtasks());
    }

    @Test
    @DisplayName("Удаление эпика")
    public void shouldAddEpicAndDelete() throws IOException, InterruptedException {
        assertEquals(200, sendDeleteRequest(epicsURL + "/3").statusCode());
        assertEquals(Collections.emptyList(), manager.getAllEpicTasks());
        assertEquals(Collections.emptyList(), manager.getAllSubtasks());
    }

    @Test
    @DisplayName("Удаление по несуществующему id")
    public void shouldReturn404CodeForDeletingById() throws IOException, InterruptedException {
        assertEquals(404, sendDeleteRequest(tasksURL + "/10").statusCode());
        assertEquals(404, sendDeleteRequest(subtasksURL + "/10").statusCode());
        assertEquals(404, sendDeleteRequest(epicsURL + "/10").statusCode());
    }

    @Test
    @DisplayName("Ошибочный запрос: метод")
    public void shouldGetTheException() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик_ошибочный_метод", "Описание");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tasksURL))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
    }

    private void initializeTasks() {
        task_1 = new Task("Задание_1", "Описание_задания_1",
                LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofMinutes(10));
        task_2 = new Task("Задание_2", "Описание_задания_1",
                LocalDateTime.of(2000, 1, 1, 0, 10), Duration.ofMinutes(10));
        epic_1 = new Epic("Эпик", "Описание_эпика");
        subtask_1 = new Subtask("Сабтаск_1", "Описание_сабтаска",
                LocalDateTime.of(2000, 1, 1, 1, 0), Duration.ofMinutes(10), 3);
        subtask_2 = new Subtask("Сабтаск_2", "Описание_сабтаска",
                LocalDateTime.of(2000, 1, 1, 1, 30), Duration.ofMinutes(10), 3);
    }

    private void addTasksToManager() {
        manager.addTask(task_1);
        manager.addTask(task_2);
        manager.addEpic(epic_1);
        manager.addSubtask(subtask_1);
        manager.addSubtask(subtask_2);
    }

    private HttpResponse sendGetRequest(String uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse sendPostRequest(String uri, Task taskToAdd) throws IOException, InterruptedException {
        String json = null;

        switch (taskToAdd.getType()) {
            case TASK: {
                json = gson.toJson(taskToAdd, Task.class);
                break;
            }
            case SUBTASK: {
                json = gson.toJson(taskToAdd, Subtask.class);
                break;
            }
            case EPIC: {
                json = gson.toJson(taskToAdd, Epic.class);
                break;
            }
        }

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse sendDeleteRequest(String uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}
