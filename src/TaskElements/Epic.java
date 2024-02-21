package TaskElements;

import TaskManager.Status;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasks;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Integer> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return super.toString() + ", subtasks_ID`s=" + subtasks;
    }
}
