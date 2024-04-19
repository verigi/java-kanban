package task.managers;

import task.managers.history_manager.HistoryManager;
import task.managers.history_manager.InMemoryHistoryManager;
import task.managers.service_manager.InMemoryTaskManager;
import task.managers.service_manager.TaskManager;

public class Managers {
    public Managers() {
    }

    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
