package task.elements;

import task.enums.Status;
import task.enums.Type;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasks;
    private Type type;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasks = new ArrayList<>();
        this.type = Type.EPIC;
    }

    public ArrayList<Integer> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public Type getType() {
        return type;
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
