package pl.foltak.mybudget.server.dao.exception;

/**
 *
 * @author mfoltak
 */
public class TransactionNotFoundException extends Exception {

    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
