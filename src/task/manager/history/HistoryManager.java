package task.manager.history;

import task.elements.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistoryList();

    void add(Task task);

    void remove(int id);

    void removeTaskType(List<Task> taskTypeList);
}
