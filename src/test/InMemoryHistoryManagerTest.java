package test;

import TaskElements.Task;
import TaskManager.HistoryManager;
import TaskManager.InMemoryHistoryManager;
import TaskManager.Status;
import org.junit.jupiter.api.*;
import java.util.List;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @Test
    @BeforeEach
    @DisplayName("Создание менеджера истории")
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("Проверка пустой истории")
    public void shouldReturnEmptyHistory() {
        Assumptions.assumeTrue(historyManager.getHistoryList().isEmpty());
    }

    @Test
    @DisplayName("Добавление задачи в историю")
    public void shouldAddTask() {
        Task task1 = new Task("Задание_1", "Тестовое задание 1", Status.NEW);
        Task task2 = new Task("Задание_2", "Тестовое задание 2", Status.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        Assertions.assertNotNull(historyManager.getHistoryList());
        Assertions.assertEquals(2, historyManager.getHistoryList().size());
    }

    @Test
    @DisplayName("Замена первого элемента при заполнении истории")
    public void s() {
        Task task1 = new Task("Задание_1", "Тестовое задание 1", Status.NEW);
        Task task2 = new Task("Задание_2", "Тестовое задание 2", Status.NEW);
        Task task3 = new Task("Задание_3", "Тестовое задание 3", Status.NEW);
        Task task4 = new Task("Задание_4", "Тестовое задание 4", Status.NEW);
        Task task5 = new Task("Задание_5", "Тестовое задание 5", Status.NEW);
        Task task6 = new Task("Задание_6", "Тестовое задание 6", Status.NEW);
        Task task7 = new Task("Задание_7", "Тестовое задание 7", Status.NEW);
        Task task8 = new Task("Задание_8", "Тестовое задание 8", Status.NEW);
        Task task9 = new Task("Задание_9", "Тестовое задание 9", Status.NEW);
        Task task10 = new Task("Задание_10", "Тестовое задание 10", Status.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);
        historyManager.add(task8);
        historyManager.add(task9);
        historyManager.add(task10);
        Assertions.assertEquals(List.of(task1, task2, task3, task4, task5, task6, task7, task8, task9, task10),
                historyManager.getHistoryList());
        Task task11 = new Task("Задание_11","Контрольное задание", Status.NEW);
        historyManager.add(task11);
        Assertions.assertNotEquals(task1, historyManager.getHistoryList().get(0));
        Assertions.assertEquals(10, historyManager.getHistoryList().size());
    }
}