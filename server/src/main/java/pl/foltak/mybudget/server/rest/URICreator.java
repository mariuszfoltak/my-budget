package pl.foltak.mybudget.server.rest;

import java.net.URI;
import pl.foltak.mybudget.server.entity.Transaction;

/**
 *
 * @author mfoltak
 */
public class URICreator {

    public static URI of(String accountName, Transaction transaction) {
        return URI.create("accounts/" + accountName + "/" + transaction.getId());
    }
}
