package pl.foltak.mybudget.server.rest;

import java.net.URI;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.entity.Transaction;
import pl.foltak.mybudget.server.entity.User;

/**
 *
 * @author mfoltak
 */
public class TransactionService {

    User user;

    Response createTransaction(String accountName, String mainCategoryName, String subCategoryName, Transaction transaction) {
        user.findAccount(accountName).addTransaction(transaction);
        user.findCategory(mainCategoryName).findCategory(subCategoryName).addTransaction(transaction);
        return Response.created(URI.create("transactions/" + accountName + "/" + transaction.getId())).build();
    }

}
