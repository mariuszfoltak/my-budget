package pl.foltak.mybudget.server.dao.exception;

/**
 *
 * @author mfoltak
 */
public class CategoryCantBeRemovedException extends Exception {

    public CategoryCantBeRemovedException(String message) {
        super(message);
    }
}
