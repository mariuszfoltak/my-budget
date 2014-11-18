package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Path("/accounts")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AccountService extends AbstractService {


    @PUT
    @Path("/")
    public Response createAccount(Account account) {
        throwConflictExceptionIfAccountAlreadyExists(account);
        getUser().addAccount(account);
        return Response.created(URI.create("account/" + account.getName())).build();
    }

    @POST
    @Path("/{account}")
    Response modifyAccount(String wallet, Account account) {
        Account currentAccount = findAccount(wallet);
        throwConflictExceptionIfAccountAlreadyExists(account);
        currentAccount.setName(account.getName());
        return Response.ok().build();
    }

    @DELETE
    @Path("/{account}")
    Response removeAccount(String WALLET) {
        Account account = findAccount(WALLET);
        throwBadRequestExceptionIfAccountHasTransactions(account);
        getUser().removeAccount(account);
        return Response.ok().build();
    }

    @GET
    List<Account> getAccounts(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(200);
        return getUser().getAccounts();
    }

    private void throwBadRequestExceptionIfAccountHasTransactions(Account account) throws BadRequestException {
        if (account.hasTransactions()) {
            throw new BadRequestException(String.format("Account '%s' has transactions and cannot be removed", account.getName()));
        }
    }

    private void throwConflictExceptionIfAccountAlreadyExists(Account account) throws ConflictException {
        if (getUser().findAccount(account.getName()) != null) {
            throw new ConflictException(String.format("Account '%s' already exists", account.getName()));
        }
    }

    private Account findAccount(String wallet) throws NotFoundException {
        return getUser().findAccount(wallet).orElseThrow(() -> {
            return new NotFoundException(String.format("Account '%s' doesn't exist", wallet));
        });
    }
}
