package TaskManager;

import TaskElements.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistoryList();

    void add(Task task);
}
