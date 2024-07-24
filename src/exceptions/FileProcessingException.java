package exceptions;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(final String message) {
        super(message);
    }
}
