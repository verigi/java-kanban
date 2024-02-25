package TaskElements;

import TaskManager.Status;

public class Subtask extends Task {

    private int epicID;

    public Subtask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return super.toString() + ", epic_ID=" + epicID;
    }
}
