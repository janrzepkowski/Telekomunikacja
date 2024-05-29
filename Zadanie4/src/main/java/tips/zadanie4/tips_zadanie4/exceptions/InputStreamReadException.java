package tips.zadanie4.tips_zadanie4.exceptions;

import java.io.IOException;

public class InputStreamReadException extends IOException {
    public InputStreamReadException() {
    }

    public InputStreamReadException(String message) {
        super(message);
    }
}
