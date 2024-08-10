package exceptions;

public class TaskTypeException extends RuntimeException {
    public TaskTypeException(final String message) {
        super(message);
    }
}
