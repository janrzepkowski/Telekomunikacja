package tips.zadanie3.tips_zadanie3.exceptions;

public class IOManagerReadException extends IOManagerGeneralException {
    public IOManagerReadException(String message) {
        super(message);
    }

    public IOManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
