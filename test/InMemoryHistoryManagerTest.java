import task.elements.Task;
import task.managers.history_manager.HistoryManager;
import task.managers.history_manager.InMemoryHistoryManager;
import task.enums.Status;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        task1.setId(1);
        Task task2 = new Task("Задание_2", "Тестовое задание 2", Status.NEW);
        task2.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);
        assertEquals(2, historyManager.getHistoryList().size());
    }

    @Test
    @DisplayName("Добавление второй задачи с уже существующим ID")
    public void shouldReturnCorrectNumberOfTaskAfterAddingNewOneWithAlreadyExistingID() {
        Task task1 = new Task("Задание_1", "Тестовое задание 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задание_2", "Тестовое задание 2", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Задание_3", "Тестовое задание 3", Status.NEW);
        task3.setId(3);
        Task task4 = new Task("Задание_4", "Тестовое задание 4", Status.NEW);
        task4.setId(4);
        Task task5 = new Task("Задание_5", "Тестовое задание 5", Status.NEW);
        task5.setId(1);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        assertEquals(4,
                historyManager.getHistoryList().size());
    }

    @Test
    @DisplayName("Удаление задачи")
    public void shouldRemoveOneTaskFromHistoryList() {
        Task task1 = new Task("Задание_1", "Тестовое задание 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задание_2", "Тестовое задание 2", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Задание_3", "Тестовое задание 3", Status.NEW);
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);
        assertEquals(List.of(task2, task3), historyManager.getHistoryList());
    }

    @Test
    @DisplayName("Проверка пустой истории после добавления и удаления всех задач")
    public void shouldReturnEmpryHistory() {
        Task task1 = new Task("Задание_1", "Тестовое задание 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задание_2", "Тестовое задание 2", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Задание_3", "Тестовое задание 3", Status.NEW);
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.remove(3);
        assertEquals(Collections.emptyList(), historyManager.getHistoryList());
    }

    @Test
    @DisplayName("Удаление задачи с несуществующим ID")
    public void shouldShowCorrectSizeOfHistoryList(){
        Task task1 = new Task("Задание_1", "Тестовое задание 1", Status.NEW);
        task1.setId(1);
        historyManager.add(task1);
        historyManager.remove(2);
        assertEquals(1,historyManager.getHistoryList().size());
    }
}