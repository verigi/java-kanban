package TaskManager;

import TaskElements.Epic;
import TaskElements.Subtask;
import TaskElements.Task;

import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Subtask addSubtask(Subtask subtask);

    Epic addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpicTasks();

    List<Subtask> getCertainEpicSubtasks(Integer epic_ID);

    void clearAllTasks();

    void clearAllSubtasks();

    void clearAllEpics();

    Task getTask(Integer ID);

    Subtask getSubtask(Integer ID);

    Epic getEpic(Integer ID);

    List<Task> getHistory();

    void deleteTaskByID(Integer ID);

    void deleteSubtaskByID(Integer ID);

    void deleteEpicByID(Integer ID);
}
