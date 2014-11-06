package pl.foltak.mybudget.server.rest;

import java.net.URI;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.User;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz.foltak@coi.gov.pl>
 */
public class AccountService {

    User user;

    Response createAccount(Account account) {
        if (user.findAccount(account.getName()) != null) {
            throw new ConflictException(String.format("Account '%s' already exists", account.getName()));
        }
        user.addAccount(account);
        return Response.created(URI.create("account/" + account.getName())).build();
    }

}
