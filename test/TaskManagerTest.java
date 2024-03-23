import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import task.manager.InMemoryTaskManager;
import task.manager.Status;
import task.manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class TaskManagerTest {
    TaskManager taskManager;
    Task task1;
    Task task2;
    Epic epic1;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    @Test
    @DisplayName("Создание менеджера с задачами, эпиком и подзадачами")
    public void createManagerWithTasksEpicAndSubtasks() {
        taskManager = new InMemoryTaskManager();
        task1 = new Task("Задача_1", "Тестовая задача 1", Status.NEW);
        task2 = new Task("Задача_2", "Тестовая задача 2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        epic1 = new Epic("Эпик", "Тестовый эпик", Status.NEW);
        taskManager.addEpic(epic1);

        subtask1 = new Subtask("Подзадача_1", "Тестовая подзадача 1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("Подзадача_2", "Тестовая подзадача 2", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Assertions.assertEquals(2, taskManager.getAllTasks().size());
        Assertions.assertEquals(List.of(task1, task2), taskManager.getAllTasks());
        Assertions.assertEquals(1, taskManager.getAllEpicTasks().size());
        Assertions.assertEquals(List.of(epic1), taskManager.getAllEpicTasks());
        Assertions.assertEquals(2, taskManager.getAllSubtasks().size());
        Assertions.assertEquals(List.of(subtask1, subtask2), taskManager.getAllSubtasks());
    }

    @Test
    @DisplayName("Добавление задачи")
    public void addingAnyKindOfTask() {
        // проверка до добавления в списки
        Assertions.assertEquals(5, taskManager.getAllTasks().size() +
                taskManager.getAllEpicTasks().size() +
                taskManager.getAllSubtasks().size());

        // добавление по задаче каждого типа
        Task task3 = new Task("Задача_3", "Тестовая задача 3", Status.NEW);
        taskManager.addTask(task3);
        Epic epic2 = new Epic("Эпик_2", "Текстовый эпик 2", Status.NEW);
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача_3", "Тестовая подзадача 3", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask3);

        // проверка после добавления
        Assertions.assertEquals(8, taskManager.getAllTasks().size() +
                taskManager.getAllEpicTasks().size() +
                taskManager.getAllSubtasks().size());
    }

    @Test
    @DisplayName("Очистка списка задач")
    public void listOfTasksShouldBeEmpty() {
        taskManager.clearAllTasks();
        taskManager.clearAllEpics();
        taskManager.clearAllSubtasks();

        Assertions.assertEquals(Collections.emptyList(), taskManager.getAllTasks());
        Assertions.assertEquals(Collections.emptyList(), taskManager.getAllEpicTasks());
        Assertions.assertEquals(Collections.emptyList(), taskManager.getAllSubtasks());
    }

    @Test
    @DisplayName("Поиск задания по ID")
    public void shouldReturnTaskByID() {
        Task task3 = new Task("Задача_3", "Тестовая задача 3", Status.NEW);
        taskManager.addTask(task3);

        Assertions.assertEquals(task3, taskManager.getTask(task3.getId()));
    }

    @Test
    @DisplayName("Поиск задания по несуществующему ID")
    public void shouldBeNullForNoSuchTaskID() {
        Assertions.assertNull(taskManager.getTask(Integer.MAX_VALUE - 1), "Существующее задание");
    }

    @Test
    @DisplayName("Поиск подзадания по ID")
    public void shouldReturnSubtaskByID() {
        Subtask subtask3 = new Subtask("Подзадача_3", "Тестовая подзадача 3", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask3);

        Assertions.assertEquals(subtask3, taskManager.getSubtask(subtask3.getId()));
    }

    @Test
    @DisplayName("Поиск подзадания по несуществующему ID")
    public void shouldBeNullForNoSuchSubtaskID() {
        Assertions.assertNull(taskManager.getSubtask(Integer.MAX_VALUE - 1), "Существующее подзадание");
    }

    @Test
    @DisplayName("Поиск эпика по ID")
    public void shouldReturnEpicByID() {
        Epic epic2 = new Epic("Эпик_2", "Тестовый эпик 2", Status.NEW);
        taskManager.addEpic(epic2);

        Assertions.assertEquals(epic2, taskManager.getEpic(epic2.getId()));
    }

    @Test
    @DisplayName("Получение эпика по несуществующему ID")
    public void shouldBeNullForNoSuchEpicID() {
        Assertions.assertNull(taskManager.getEpic(Integer.MAX_VALUE - 1), "Существующее подзадание");
    }

    @Test
    @DisplayName("Удаление задания/ий по ID")
    public void shouldReturnListOfTaskWithoutRemovedOnes() {
        Task task3 = new Task("Задача_3", "Тестовая задача 3", Status.NEW);
        Task task4 = new Task("Задача_4", "Тестовая задача 4", Status.NEW);
        Task task5 = new Task("Задача_5", "Тестовая задача 5", Status.NEW);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        taskManager.addTask(task5);
        // удаление одного задания
        taskManager.deleteTaskByID(task1.getId());
        Assertions.assertEquals(List.of(task2, task3, task4, task5), taskManager.getAllTasks());
        // удаление нескольких подзаданий
        taskManager.deleteTaskByID(task2.getId());
        taskManager.deleteTaskByID(task3.getId());
        Assertions.assertEquals(List.of(task4, task5), taskManager.getAllTasks());
    }

    @Test
    @DisplayName("Удаление подзадания/ий по ID")
    public void shouldReturnListOfSubtaskWithoutRemovedOnes() {
        Subtask subtask3 = new Subtask("Подзадача_3", "Тестовая подзадача 3", Status.NEW, epic1.getId());
        Subtask subtask4 = new Subtask("Подзадача_4", "Тестовая подзадача 4", Status.NEW, epic1.getId());
        Subtask subtask5 = new Subtask("Подзадача_5", "Тестовая подзадача 5", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);
        // удаление одного подзадания
        taskManager.deleteSubtaskByID(subtask1.getId());
        Assertions.assertEquals(List.of(subtask2, subtask3, subtask4, subtask5), taskManager.getAllSubtasks());
        // удаление нескольких подзаданий
        taskManager.deleteSubtaskByID(subtask2.getId());
        taskManager.deleteSubtaskByID(subtask3.getId());
        Assertions.assertEquals(List.of(subtask4, subtask5), taskManager.getAllSubtasks());
    }

    @Test
    @DisplayName("Удаление эпика/ов по ID")
    public void shouldReturnListOfEpicWithoutRemovedOnes() {
        Epic epic2 = new Epic("Эпик_2", "Текстовый эпик 2", Status.NEW);
        Epic epic3 = new Epic("Эпик_3", "Текстовый эпик 3", Status.NEW);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        // удаление одного эпика
        taskManager.deleteEpicByID(epic2.getId());
        Assertions.assertEquals(List.of(epic1, epic3), taskManager.getAllEpicTasks());
        // удаление нескольких эпиков
        taskManager.deleteEpicByID(epic1.getId());
        Assertions.assertEquals(List.of(epic3), taskManager.getAllEpicTasks());
    }

    @Test
    @DisplayName("Удаление эпика - удаление подзаданий")
    public void shouldReturnEmptyListOfSubtasks() {
        taskManager.deleteEpicByID(epic1.getId());
        Assertions.assertEquals(Collections.emptyList(), taskManager.getAllSubtasks());
    }

    @Test
    @DisplayName("Поиск эпика по подзадаче")
    public void shouldCheckIfSubtaskHasEpicID() {
        Assertions.assertEquals(epic1.getId(), subtask1.getEpicID());
        Assertions.assertEquals(epic1.getId(), subtask2.getEpicID());
    }

    @Test
    @DisplayName("Обновление статуса задачи")
    public void shouldUpdateTaskStatus() {
        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);
        Assertions.assertEquals(Status.IN_PROGRESS, task1.getStatus());
        Assertions.assertEquals(Status.DONE, task2.getStatus());
    }

    @Test
    @DisplayName("Обновление статуса подзадачи")
    public void shouldUpdateSubtaskStatus() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        Assertions.assertEquals(Status.IN_PROGRESS, subtask1.getStatus());
        Assertions.assertEquals(Status.DONE, subtask2.getStatus());
    }

    @Test
    @DisplayName("Получение списка задач")
    public void shouldReturnTaskList() {
        Assertions.assertEquals(List.of(task1, task2), taskManager.getAllTasks());
    }

    @Test
    @DisplayName("Получение списка подзадач")
    public void shouldReturnSubtaskList() {
        Assertions.assertEquals(List.of(subtask1, subtask2), taskManager.getAllSubtasks());
    }

    @Test
    @DisplayName("Получение списка эпиков")
    public void shouldReturnEpicList() {
        Assertions.assertEquals(List.of(epic1), taskManager.getAllEpicTasks());
    }

    @Test
    @DisplayName("Проверка истории вызовов")
    public void shouldReturnListOfRequests() {
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);
        taskManager.getSubtask(5);
        Assertions.assertEquals(List.of(task1, task2, epic1, subtask1, subtask2), taskManager.getHistory());
    }

    @Test
    @DisplayName("Проверка на дубли")
    public void shouldReturnListWithoutDuplicates() {
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.getTask(1);
        Assertions.assertEquals(List.of(task2, task1), taskManager.getHistory());
    }

    @Test
    @DisplayName("Проверка удаления списка всех задач, проверка удаления из истории")
    public void shouldReturnEmptyListOfTasks() {
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.clearAllTasks();
        Assertions.assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @DisplayName("Проверка удаления списка всех эпиков, проверка удаления из истории")
    public void shouldReturnEmptyListOfEpics() {
        taskManager.getEpic(3);
        taskManager.clearAllEpics();
        Assertions.assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @DisplayName("Проверка удаления списка всех подзадач, проверка удаления из истории")
    public void shouldReturnEmptyListOfSubs() {
        taskManager.getSubtask(4);
        taskManager.getSubtask(5);
        taskManager.clearAllSubtasks();
        Assertions.assertEquals(0, taskManager.getHistory().size());
    }
}