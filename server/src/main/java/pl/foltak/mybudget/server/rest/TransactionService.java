package pl.foltak.mybudget.server.rest;

import java.net.URI;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.dto.TransactionDTO;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.Tag;
import pl.foltak.mybudget.server.entity.Transaction;
import pl.foltak.mybudget.server.entity.User;

/**
 *
 * @author mfoltak
 */
public class TransactionService {

    User user;

    Transaction convert(TransactionDTO transactionDTO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Response createTransaction(String accountName, String mainCategoryName, String subCategoryName, TransactionDTO transactionDTO) {
        final Account account = findAccount(accountName);
        final Category mainCategory = findMainCategory(mainCategoryName);
        final Category subCategory = findSubCategory(mainCategory, subCategoryName);
        final Transaction transaction = convert(transactionDTO);
        account.addTransaction(transaction);
        subCategory.addTransaction(transaction);
        if (transactionDTO.getTags() != null) {
            for (String tagName : transactionDTO.getTags()) {
                transaction.addTag(findOrCreateTag(tagName));
            }
        }
        return Response.created(URI.create("transactions/" + accountName + "/" + transaction.getId())).build();
    }

    private Category findSubCategory(Category mainCategory, String subCategoryName) throws NotFoundException {
        final Category subCategory = mainCategory.findCategory(subCategoryName);
        if (subCategory == null) {
            throw new NotFoundException(String.format("Category '%s' doesn't exist", subCategoryName));
        }
        return subCategory;
    }

    private Category findMainCategory(String mainCategoryName) throws NotFoundException {
        final Category mainCategory = user.findCategory(mainCategoryName);
        if (mainCategory == null) {
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

    Tag findOrCreateTag(String firstTagName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}