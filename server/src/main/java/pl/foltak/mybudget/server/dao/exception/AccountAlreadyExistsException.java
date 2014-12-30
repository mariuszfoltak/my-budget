package pl.foltak.mybudget.server.dao.exception;

/**
 *
 * @author mfoltak
 */
public class AccountAlreadyExistsException extends Exception {

    public static AccountAlreadyExistsException of(String accountName) {
        String message = String.format("Account %s already exists", accountName);
        return new AccountAlreadyExistsException(message);
    }
    
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
    
}
