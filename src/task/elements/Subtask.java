package task.elements;

import task.enums.Status;
import task.enums.Type;

public class Subtask extends Task {

    private int epicID;
    private Type type;

    public Subtask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
        this.type = Type.SUBTASK;
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
