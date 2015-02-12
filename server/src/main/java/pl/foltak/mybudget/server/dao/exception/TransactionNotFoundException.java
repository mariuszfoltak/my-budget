package pl.foltak.mybudget.server.dao.exception;

/**
 *
 * @author mfoltak
 */
public class TransactionNotFoundException extends Exception {

    static public TransactionNotFoundException of(long id) {
        String message = String.format("Transaction with id: %s doesn't exist", id);
        return new TransactionNotFoundException(message);
    }
    
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
