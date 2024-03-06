package TaskManager;

import TaskElements.Task;

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
