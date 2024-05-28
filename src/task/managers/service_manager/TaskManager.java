package task.managers.service_manager;

import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;

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

    List<Subtask> getCertainEpicSubtasks(Integer epicID);

    void clearAllTasks();

    void clearAllSubtasks();

    void clearAllEpics();

    Task getTask(Integer id);

    Subtask getSubtask(Integer id);

    Epic getEpic(Integer id);

    List<Task> getHistory();

    void deleteTaskByID(Integer id);

    void deleteSubtaskByID(Integer id);

    void deleteEpicByID(Integer id);
}
