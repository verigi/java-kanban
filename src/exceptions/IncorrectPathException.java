package exceptions;

public class IncorrectPathException extends RuntimeException {
    public IncorrectPathException(final String message) {
        super(message);
    }
}
