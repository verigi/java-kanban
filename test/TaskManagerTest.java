import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import enums.Status;
import task.managers.service_manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

abstract class TaskManagersTest<T extends TaskManager> {
    protected T manager;

    protected abstract T init();

    protected Task task_1;
    protected Task task_2;
    protected Epic epic_1;
    protected Subtask subtask_1;
    protected Subtask subtask_2;

    @BeforeEach
    // установка стандартных значений
    public void taskInitialization(){
        init();
        task_1 = new Task("Тестовое_задание_1", "Описание_задания_1",
                LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofMinutes(10));
        task_2 = new Task("Тестовое_задание_2", "Описание_задания_2",
                LocalDateTime.of(2000, 1, 1, 1, 0), Duration.ofMinutes(10));
        epic_1 = new Epic("Тестовый_эпик_1", "Описание_эпика");
        subtask_1 = new Subtask("Тестовый_сабтаск_1", "Описание_сабтаска_1",
                LocalDateTime.of(2000, 1, 1, 2, 0), Duration.ofMinutes(10), 3);
        subtask_2 = new Subtask("Тестовый_сабтаск_2", "Описание_сабтаска_2",
                LocalDateTime.of(2000, 1, 1, 3, 0), Duration.ofMinutes(10), 3);
    }

    protected void addingToManager() {
        manager.addTask(task_1);
        manager.addTask(task_2);
        manager.addEpic(epic_1);
        manager.addSubtask(subtask_1);
        manager.addSubtask(subtask_2);
    }

    @Test
    @DisplayName("Добавление заданий в менеджер")
    public void saveInManagerTest() {
        addingToManager();
        // проверка наличия
        Assertions.assertEquals(List.of(task_1, task_2), manager.getAllTasks());
        Assertions.assertEquals(List.of(epic_1), manager.getAllEpicTasks());
        Assertions.assertEquals(List.of(subtask_1, subtask_2), manager.getAllSubtasks());
        // проверка id
        Assertions.assertEquals(1, task_1.getId());
        Assertions.assertEquals(2, task_2.getId());
        Assertions.assertEquals(3, epic_1.getId());
        Assertions.assertEquals(4, subtask_1.getId());
        Assertions.assertEquals(5, subtask_2.getId());
        // проверка имени
        Assertions.assertEquals("Тестовое_задание_1", task_1.getName());
        Assertions.assertEquals("Тестовое_задание_2", task_2.getName());
        Assertions.assertEquals("Тестовый_эпик_1", epic_1.getName());
        Assertions.assertEquals("Тестовый_сабтаск_1", subtask_1.getName());
        Assertions.assertEquals("Тестовый_сабтаск_2", subtask_2.getName());
    }

    @Test
    @DisplayName("Проверка удаления всех типов заданий")
    public void deletingAllKindOfTasksTest() {
        addingToManager();
        manager.clearAllTasks();
        manager.clearAllEpics();
        manager.clearAllSubtasks();
        Assertions.assertTrue(manager.getAllTasks().isEmpty());
        Assertions.assertTrue(manager.getAllEpicTasks().isEmpty());
        Assertions.assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка удаления типов заданий по id")
    public void deletingAllKindOfTasksByIdTest() {
        addingToManager();
        // удаление тестовое_задание_1
        manager.deleteTaskByID(1);
        // удаление тестовый_сабтаск_1
        manager.deleteSubtaskByID(4);
        Assertions.assertEquals(List.of(task_2), manager.getAllTasks());
        Assertions.assertEquals(List.of(subtask_2), manager.getAllSubtasks());
        // удаление эпика подразумевает удаление всех его сабтасков
        manager.deleteEpicByID(3);
        Assertions.assertTrue(manager.getAllEpicTasks().isEmpty());
        Assertions.assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка поиска заданий по ID и записи в историю")
    public void gettingAllKindOfTasksById() {
        addingToManager();
        // возвращает по id нужный таск
        Assertions.assertEquals(task_1, manager.getTask(1));
        Assertions.assertEquals(task_2, manager.getTask(2));
        Assertions.assertEquals(epic_1, manager.getEpic(3));
        Assertions.assertEquals(subtask_1, manager.getSubtask(4));
        Assertions.assertEquals(subtask_2, manager.getSubtask(5));
        // проверка записи в историю
        Assertions.assertEquals(List.of(task_1, task_2, epic_1, subtask_1, subtask_2), manager.getHistory());
    }

    @Test
    @DisplayName("Проверка поиска задания по несуществующему ID")
    public void noSuchIdSearch() {
        addingToManager();
        Assertions.assertThrows(NoSuchElementException.class, () -> manager.getTask(10));
        Assertions.assertThrows(NoSuchElementException.class, () -> manager.getEpic(10));
        Assertions.assertThrows(NoSuchElementException.class, () -> manager.getSubtask(10));
        Assertions.assertTrue(manager.getHistory().isEmpty());
    }


    @Test
    @DisplayName("Проверка подвязка сабстасков к эпикам")
    public void epicSearchBySubtasksID() {
        addingToManager();
        Assertions.assertEquals(epic_1.getId(), subtask_1.getEpicID());
        Assertions.assertEquals(epic_1.getId(), subtask_2.getEpicID());
    }

    @Test
    @DisplayName("Проверка обновление данных задания")
    public void updatingTasksData() {
        addingToManager();
        task_1.setName("Обновленное_имя_1");
        manager.updateTask(task_1);
        Assertions.assertEquals("Обновленное_имя_1", manager.getTask(1).getName());
    }

    @Test
    @DisplayName("Проверка обновление данных сабтасков")
    public void updatingSubtasksData() {
        addingToManager();
        subtask_1.setName("Обновленное_имя_1");
        manager.updateSubtask(subtask_1);
        Assertions.assertEquals("Обновленное_имя_1", manager.getSubtask(4).getName());
    }

    @Test
    @DisplayName("Проверка обновление данных эпика")
    public void updatingEpicData() {
        addingToManager();
        epic_1.setName("Обновленное_имя_1");
        manager.updateEpic(epic_1);
        Assertions.assertEquals("Обновленное_имя_1", manager.getEpic(3).getName());
    }

    @Test
    @DisplayName("Проверка обновления статуса/времени эпика")
    public void updatingEpicStatusDateData() {
        addingToManager();
        Assertions.assertEquals(Status.NEW, epic_1.getStatus());
//ПРОВЕРКА СТАТУСОВ
        //Сабтаск_1 - в процессе, Сабтаск_2 - новый
        subtask_1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask_1);
        Assertions.assertEquals(Status.IN_PROGRESS, epic_1.getStatus());
        //Сабтаск_1 - в процессе, Сабтаск_2 - в процессе
        subtask_2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask_2);
        Assertions.assertEquals(Status.IN_PROGRESS, epic_1.getStatus());
        //Сабтаск_1 - выполнен, Сабтаск_2 - в процессе
        subtask_1.setStatus(Status.DONE);
        manager.updateSubtask(subtask_1);
        Assertions.assertEquals(Status.IN_PROGRESS, epic_1.getStatus());
        //Сабтаск_1 - выполнен, Сабтаск_2 - выполнен
        subtask_2.setStatus(Status.DONE);
        manager.updateSubtask(subtask_2);
        Assertions.assertEquals(Status.DONE, epic_1.getStatus());
//ПРОВЕРКА ВРЕМЕНИ
        //проверка времени старта эпика
        Assertions.assertEquals(subtask_1.getStartTime(), epic_1.getStartTime());
        //проверка времени конца эпика
        Assertions.assertEquals(subtask_2.getEndTime(), epic_1.getEndTime());
        //проверка длительности эпика
        Assertions.assertEquals(subtask_1.getDuration().plus(subtask_2.getDuration()), epic_1.getDuration());
    }
}