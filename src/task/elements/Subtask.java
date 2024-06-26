package task.elements;

import task.enums.Status;
import task.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicID;
    private Type type;


    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.type = Type.SUBTASK;
        this.setStatus(Status.NEW);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.type = Type.SUBTASK;
        this.epicID = epicID;
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, int epicID) {
        super(name, description, startTime, duration);
        this.type = Type.SUBTASK;
        this.setStatus(Status.NEW);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, LocalDateTime startTime, Duration duration,
                   int epicID) {
        super(name, description, status, startTime, duration);
        this.type = Type.SUBTASK;
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return super.toString() + ", epic_ID=" + epicID;
    }
}
