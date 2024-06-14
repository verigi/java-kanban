package task.elements;

import task.enums.Status;
import task.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasks;
    private Type type;
    private LocalDateTime defaultStartTime = LocalDateTime.of(1, 1, 1, 1, 1);
    private Duration defaultDuration = Duration.ofMinutes(0);
    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.type = Type.EPIC;
        this.setStatus(Status.NEW);
        this.setStartTime(defaultStartTime);
        this.setDuration(defaultDuration);
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
        if (subtasks.contains(id)) {
            subtasks.remove(id);
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void removeAllSubtask() {
        subtasks.clear();
    }

    @Override
    public String toString() {
        return super.toString() + ", subtasks_ID`s=" + subtasks;
    }
}
