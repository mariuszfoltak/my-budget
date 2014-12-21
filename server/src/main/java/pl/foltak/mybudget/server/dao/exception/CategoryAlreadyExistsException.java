package pl.foltak.mybudget.server.dao.exception;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class CategoryAlreadyExistsException extends Exception {

    public static CategoryAlreadyExistsException of(String categoryName) {
        String message = String.format("Category %s already exists", categoryName);
        return new CategoryAlreadyExistsException(message);
    }

    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
    
}
