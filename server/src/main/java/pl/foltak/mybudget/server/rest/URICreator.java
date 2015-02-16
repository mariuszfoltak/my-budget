package pl.foltak.mybudget.server.rest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.foltak.mybudget.server.entity.Transaction;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public final class URICreator {

    public static URI of(String accountName, Transaction transaction) {
        return URI.create("accounts/" + accountName + "/" + transaction.getId());
    }
    private String first;

    private URICreator() {
    }

    public URICreator(String first) {
        this.first = encode(first);
    }

    public URI create(String... strings) {
        StringBuilder stringBuilder = new StringBuilder(first);
        for (String next : strings) {
            stringBuilder.append('/').append(encode(next));
        }
        return URI.create(stringBuilder.toString());
    }

    private String encode(String wallet) throws RuntimeException {
        try {
            return URLEncoder.encode(wallet, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
