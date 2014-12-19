package pl.foltak.mybudget.server.dao.exception;

/**
 *
 * @author mfoltak
 */
public class CategoryNotFoundException extends Exception {

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCategoryName() {
        return null;
    }
    
}
