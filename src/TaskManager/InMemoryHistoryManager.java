package TaskManager;

import TaskElements.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public List<Task> getHistoryList() {
        return new ArrayList<>(historyList);
    }

    @Override
    public void add(Task task) {
        if (isHistoryFull()) {
            historyList.remove(0);
            historyList.add(task);
        } else {
            historyList.add(task);
        }

    }

    private boolean isHistoryFull() {
        if (historyList.size() == 10) {
            return true;
        }
        return false;
    }
}
