package tips.zadanie4.tips_zadanie4.exceptions;

public class NullArgException extends NullPointerException {
    public NullArgException() {
    }

    public NullArgException(String s) {
        super(s);
    }
}
