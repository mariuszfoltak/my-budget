package pl.foltak.mybudget.server.rest;

import java.net.URI;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.Transaction;
import pl.foltak.mybudget.server.entity.User;

/**
 *
 * @author mfoltak
 */
public class TransactionService {

    User user;

    Response createTransaction(String accountName, String mainCategoryName, String subCategoryName, Transaction transaction) {
        final Account account = findAccount(accountName);
        final Category mainCategory = findMainCategory(mainCategoryName);
        final Category subCategory = findSubCategory(mainCategory, subCategoryName);
        account.addTransaction(transaction);
        subCategory.addTransaction(transaction);
        return Response.created(URI.create("transactions/" + accountName + "/" + transaction.getId())).build();
    }

    private Category findSubCategory(Category mainCategory, String subCategoryName) throws NotFoundException {
        final Category subCategory = mainCategory.findCategory(subCategoryName);
        if(subCategory==null) {
            throw new NotFoundException(String.format("Category '%s' doesn't exist", subCategoryName));
        }
        return subCategory;
    }

    private Category findMainCategory(String mainCategoryName) throws NotFoundException {
        final Category mainCategory = user.findCategory(mainCategoryName);
        if(mainCategory==null) {
            throw new NotFoundException(String.format("Category '%s' doesn't exist", mainCategoryName));
        }
        return mainCategory;
    }

    private Account findAccount(String accountName) throws NotFoundException {
        final Account account = user.findAccount(accountName);
        if (account == null) {
            throw new NotFoundException(String.format("Account '%s' doesn't exist", accountName));
        }
        return account;
    }

}
