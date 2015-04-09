package pl.foltak.mybudget.server.dao.exception;

/**
 *
 * @author mfoltak
 */
public class AccountNotFoundException extends Exception {

    public static AccountNotFoundException of(String accountName) {
        String message = String.format("Account %s doesn't exist", accountName);
        return new AccountNotFoundException(message);
    }

    public static AccountNotFoundException of(Long accountId) {
        String message = String.format("Account %d doesn't exist", accountId);
        return new AccountNotFoundException(message);
    }
    
    public AccountNotFoundException(String message) {
        super(message);
    }
    
}
