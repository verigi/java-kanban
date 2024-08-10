package task.elements;

import enums.Status;
import enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task implements Comparable {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected Type type;
    protected LocalDateTime startTime;
    protected Duration duration;
    protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String name, String description) {
        this.type = Type.TASK;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String name, String description, Status status) {
        this.type = Type.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.type = Type.TASK;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.type = Type.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() throws NullPointerException {
        try {
            return startTime.plusMinutes(duration.toMinutes());
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        Task newTask = (Task) o;
        return Objects.equals(id, newTask.id)
                && Objects.equals(name, newTask.name)
                && Objects.equals(description, newTask.description)
                && Objects.equals(status, newTask.status);
    }

    @Override
    public int hashCode() {
        int hash = 17 + id;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        if (description != null) {
            hash = hash + description.hashCode();
        }
        if (status != null) {
            hash = hash + status.hashCode();
        }
        return hash *= 31;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", start time='" + getStartTime().format(dateTimeFormatter) + '\'' +
                ", end time='" + getEndTime().format(dateTimeFormatter) + '\'' +
                ", duration='" + String.format("%02d:%02d", getDuration().toHoursPart(), getDuration().toMinutesPart()) + '\'';
    }

    @Override
    public int compareTo(Object o) {
        return this.getStartTime().compareTo(((Task) o).startTime);
    }
}
