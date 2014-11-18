package pl.foltak.mybudget.server.rest;

import java.net.URI;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.dto.TransactionDTO;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.Tag;
import pl.foltak.mybudget.server.entity.Transaction;

/**
 *
 * @author mfoltak
 */
public class TransactionService extends AbstractService {

    Transaction convert(TransactionDTO transactionDTO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Response createTransaction(String accountName, TransactionDTO transactionDTO) {
        final Account account = findAccount(accountName);
        final Category subCategory = getTargetCategory(transactionDTO);
        final Transaction transaction = convert(transactionDTO);
        account.addTransaction(transaction);
        subCategory.addTransaction(transaction);
        if (transactionDTO.getTags() != null) {
            for (String tagName : transactionDTO.getTags()) {
                transaction.addTag(findOrCreateTag(tagName));
            }
        }
        return Response.created(
                URI.create("transactions/" + accountName + "/" + transaction.getId())).build();
    }

    private Category findSubCategory(Category mainCategory, String subCategoryName) throws
            NotFoundException {
        final Category subCategory = mainCategory.findCategory(subCategoryName);
        if (subCategory == null) {
            throw new NotFoundException(
                    String.format("Category '%s' doesn't exist", subCategoryName));
        }
        return subCategory;
    }

    private Account findAccount(String accountName) throws NotFoundException {
        return getUser().findAccount(accountName).orElseThrow(() -> {
            return new NotFoundException(String.format("Account '%s' doesn't exist", accountName));
        });
    }

    Tag findOrCreateTag(String firstTagName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Response removeTransaction(String accountName, long transactionId) {
        final Account account = findAccount(accountName);
        final Transaction transaction = account.findTransaction(transactionId);
        if (transaction == null) {
            throw new NotFoundException(String.format("Transaction with id=%s doesn't exist",
                    transactionId));
        }
        account.removeTransaction(transaction);
        return Response.ok().build();
    }

    Response modifyTransaction(String WALLET, TransactionDTO transactionDTO) {
        Transaction transaction = findAccount(WALLET).findTransaction(transactionDTO.getId());
        if (transaction == null) {
            throw new NotFoundException(String.format("Transaction with id=%s doesn't exist",
                    transactionDTO.getId()));
        }
        transaction = updateTransaction(transactionDTO, transaction);
        Category subCategory = getTargetCategory(transactionDTO);
        subCategory.addTransaction(transaction);
        transaction.getTags().clear();
        transactionDTO.getTags();
        for (String tagName : transactionDTO.getTags()) {
            transaction.addTag(findOrCreateTag(tagName));
        }
        return Response.ok().build();
    }

    private Category getTargetCategory(TransactionDTO transactionDTO) throws NotFoundException {
        String categoryPath = transactionDTO.getCategoryPath();
        String[] categoriesNames = categoryPath.split("/");
        Category mainCategory = findMainCategory(categoriesNames[0]);
        Category subCategory = findSubCategory(mainCategory, categoriesNames[1]);
        return subCategory;
    }

    Transaction updateTransaction(TransactionDTO transactionDTO, Transaction mock) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
