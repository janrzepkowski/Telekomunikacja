package tips.zadanie4.tips_zadanie4.exceptions;

public class LineWithGivenParametersNotSupported extends IllegalArgumentException {
    public LineWithGivenParametersNotSupported(String s) {
        super(s);
    }

    public LineWithGivenParametersNotSupported(String message, Throwable cause) {
        super(message, cause);
    }
}
