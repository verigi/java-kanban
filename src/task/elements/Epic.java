package task.elements;

import task.manager.Status;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasks;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public void addSubtask(Integer id) {
        subtasks.add(id);
    }

    public void removeSubtask(Integer id) {
        if (subtasks.contains(id)) {
            subtasks.remove(id);
        }
    }

    public void removeAllSubtask() {
        subtasks.clear();
    }

    @Override
    public String toString() {
        return super.toString() + ", subtasks_ID`s=" + subtasks;
    }
}
