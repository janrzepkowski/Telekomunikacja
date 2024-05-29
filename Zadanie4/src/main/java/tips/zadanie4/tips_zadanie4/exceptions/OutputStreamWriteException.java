package tips.zadanie4.tips_zadanie4.exceptions;

import java.io.IOException;

public class OutputStreamWriteException extends IOException {
    public OutputStreamWriteException() {
    }

    public OutputStreamWriteException(String message) {
        super(message);
    }
}
