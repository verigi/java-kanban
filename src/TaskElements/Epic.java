package TaskElements;

import java.util.ArrayList;

public class EpicTask extends Task {

    private ArrayList<SubTask> subtasks;

    public EpicTask(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return super.toString() + ", subtasks=" + subtasks;
    }
}
