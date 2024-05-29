package tips.zadanie4.tips_zadanie4.exceptions;

import javax.sound.sampled.LineUnavailableException;

public class LineException extends LineUnavailableException {
    public LineException() {
    }

    public LineException(String message) {
        super(message);
    }
}
