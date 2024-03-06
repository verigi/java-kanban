package test;

import TaskManager.*;
import TaskElements.Epic;
import TaskElements.Subtask;
import TaskManager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class EpicTest {
    private TaskManager taskManager;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;


    @BeforeEach
    @Test
    @DisplayName("Создание менеджера с эпиком и подзадачами")
    public void createManagerWithEpicAndSubtasks() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("Эпик", "Тестовый эпик", Status.NEW);
        taskManager.addEpic(epic);

        subtask1 = new Subtask("Подзадача_1", "Тестовая подзадача 1", Status.NEW, epic.getId());
        subtask2 = new Subtask("Подзадача_2", "Тестовая подзадача 2", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        //Эпик добавлен в хранилище?
        Assertions.assertEquals(1, taskManager.getAllEpicTasks().size());
        //Сабтаски добавлены в хранилище?
        Assertions.assertEquals(2, taskManager.getAllSubtasks().size());
        //Сабтаски привязаны к эпику?
        Assertions.assertEquals(2, taskManager.getCertainEpicSubtasks(epic.getId()).size());
        Assertions.assertEquals(List.of(subtask1, subtask2), taskManager.getCertainEpicSubtasks(epic.getId()));
    }

    @Test
    @DisplayName("Обновление статуса: сабтаски NEW, эпик - NEW")
    public void statusOfEpicShouldBeNewWhenAllSubtasksHaveNewStatuses() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    @DisplayName("Обновление статуса: сабтаски DONE, эпик - DONE")
    public void statusOfEpicShouldBeDoneWhenAllSubtasksHaveDoneStatuses() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    @DisplayName("Обновление статуса: сабтаски DONE и NEW, эпик - IN_PROGRESS")
    public void statusOfEpicShouldBeInProgressWhenSubtasksHaveDoneAndNewStatuses() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Обновление статуса: сабтаски NEW и IN_PROGRESS, эпик - IN_PROGRESS")
    public void statusOfEpicShouldBeInProgressWhenSubtasksHaveNewAndInProgressStatuses() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Обновление статуса: сабтаски IN_PROGRESS, эпик - IN_PROGRESS")
    public void statusOfEpicShouldBeInProgressWhenSubtasksHaveInProgressStatuses() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Обновление статуса: сабтаски IN_PROGRESS и DONE, эпик - IN_PROGRESS")
    public void statusOfEpicShouldBeInProgressWhenSubtasksHaveInProgressAndDoneStatuses() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

}