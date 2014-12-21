package pl.foltak.mybudget.server.dao.exception;

/**
 *
 * @author mfoltak
 */
public class CategoryNotFoundException extends Exception {
    
    public static CategoryNotFoundException of(String categoryName) {
        String message = String.format("Category %s doesn't exist", categoryName);
        return new CategoryNotFoundException(message);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public String getCategoryName() {
        return null;
    }
    
}
