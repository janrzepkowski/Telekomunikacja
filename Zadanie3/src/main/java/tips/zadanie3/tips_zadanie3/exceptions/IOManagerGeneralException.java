package tips.zadanie3.tips_zadanie3.exceptions;

import java.io.IOException;

public class IOManagerGeneralException extends IOException {
    public IOManagerGeneralException(String message) {
        super(message);
    }

    public IOManagerGeneralException(String message, Throwable cause) {
        super(message, cause);
    }
}
