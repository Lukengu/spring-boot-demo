package pro.novatech.solutions.api.banking.services.exceptions;

public class InsufficientBalanceException extends Exception{

    public InsufficientBalanceException(String message) {
        super(message);
    }
    public InsufficientBalanceException(Throwable throwable) {
        super(throwable);
    }

    public InsufficientBalanceException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
