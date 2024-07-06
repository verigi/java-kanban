package task.elements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import task.enums.Status;
import task.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;


public class Subtask extends Task {

    private int epicID;



    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.setType(Type.SUBTASK);
        this.setStatus(Status.NEW);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.setType(Type.SUBTASK);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, int epicID) {
        super(name, description, startTime, duration);
        this.setType(Type.SUBTASK);
        this.setStatus(Status.NEW);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, LocalDateTime startTime, Duration duration,
                   int epicID) {
        super(name, description, status, startTime, duration);
        this.setType(Type.SUBTASK);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public Type getType() {
        return this.getType();
    }

    @Override
    public String toString() {
        return super.toString() + ", epic_ID=" + epicID;
    }
}
