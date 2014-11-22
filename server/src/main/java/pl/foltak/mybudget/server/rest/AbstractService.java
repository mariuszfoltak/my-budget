package pl.foltak.mybudget.server.rest;

import javax.ws.rs.NotFoundException;
import pl.foltak.mybudget.server.entity.Account;
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
            return new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, categoryName));
        });
    }

    protected Category findSubCategory(Category mainCategory, String subCategoryName)
            throws NotFoundException {
        return mainCategory.findSubCategory(subCategoryName).orElseThrow(() -> {
            return new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, subCategoryName));
        });
    }

    protected Account findAccount(String wallet) throws NotFoundException {
        return getUser().findAccount(wallet).orElseThrow(() -> {
            return new NotFoundException(String.format(ACCOUNT_DOESNT_EXIST, wallet));
        });
    }
    private static final String ACCOUNT_DOESNT_EXIST = "Account '%s' doesn't exist";

}
