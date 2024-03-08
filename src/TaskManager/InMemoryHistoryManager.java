package TaskManager;

import TaskElements.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> historyList = new ArrayList<>();

    @Override
    public List<Task> getHistoryList() {
        return new ArrayList<>(historyList);
    }

    @Override
    public void add(Task task) {
        if (historyList.size() == 10) {
            historyList.remove(0);
            historyList.add(task);
            return;
        }
        historyList.add(task);
    }
}
