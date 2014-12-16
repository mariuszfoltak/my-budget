package pl.foltak.mybudget.server.dao.exception;

/**
 *
 * @author mfoltak
 */
public class CategoryCantBeRemovedException extends Exception {

    public CategoryCantBeRemovedException() {
    }

    public CategoryCantBeRemovedException(String message) {
        super(message);
    }

    public CategoryCantBeRemovedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryCantBeRemovedException(Throwable cause) {
        super(cause);
    }
    
}
