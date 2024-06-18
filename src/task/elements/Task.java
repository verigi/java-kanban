package task.elements;

import task.enums.Status;
import task.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task implements Comparable {
    private int id;
    private String name;
    private String description;
    private Status status;
    private Type type;
    private LocalDateTime startTime;
    private Duration duration;
    protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM-dd-yyyy HH:mm");

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.setStatus(Status.NEW);
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.type = Type.TASK;
        this.status = Status.NEW;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.type = Type.TASK;
        this.status = status;
        this.name = name;
        this.description = description;
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

    public LocalDateTime getEndTime() {
        try {
            return startTime.plusMinutes(duration.toMinutes());
        } catch (NullPointerException e) {
            System.out.println("Не обозначено время начала/продолжительность задачи");
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
