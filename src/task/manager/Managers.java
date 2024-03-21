package task.manager;

import task.manager.history.HistoryManager;
import task.manager.history.InMemoryHistoryManager;

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
