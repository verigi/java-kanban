package task.elements;

import task.enums.Status;
import task.enums.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasks;
    private final Type type;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.type = Type.EPIC;
        this.setStatus(Status.NEW);
        subtasks = new ArrayList<>();
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
        subtasks.remove(id);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void removeAllSubtask() {
        subtasks.clear();
    }

    @Override
    public String toString() {
        return super.toString() + ", subtasks_ID`s=" + subtasks;
    }
}
