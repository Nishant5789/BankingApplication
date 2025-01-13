package exceptions;

public class AmountIsValidException extends RuntimeException {
    public AmountIsValidException(String message) {
        super(message);
    }
}

