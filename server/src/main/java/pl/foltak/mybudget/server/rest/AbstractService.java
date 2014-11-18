package pl.foltak.mybudget.server.rest;

import javax.ws.rs.NotFoundException;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.User;

/**
 *
 * @author mfoltak
 */
public abstract class AbstractService {
    protected static final String CATEGORY_DOESNT_EXIST = "Category '%s' doesn't exist";
    private User user;

    /**
     * @return the user
     */
    protected User getUser() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    protected Category findMainCategory(String categoryName) throws NotFoundException {
        return getUser().findCategory(categoryName).orElseThrow(() -> {
            return new NotFoundException(String.format(CategoryService.CATEGORY_DOESNT_EXIST, categoryName));
        });
    }
    
}
