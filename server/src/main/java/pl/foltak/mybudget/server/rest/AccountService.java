package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 * The account service.
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Path("/accounts")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AccountService extends AbstractService {

    private static final String ACCOUNT_HAS_TRANSACTIONS = "Account '%s' has transactions and cannot be removed";
    private static final String ACCOUNT_ALREADY_EXISTS = "Account '%s' already exists";

    @PUT
    @Path(value = "/")
    public Response createAccount(Account account) {
        throwConflictExceptionIfAccountAlreadyExists(account);
        getUser().addAccount(account);
        return Response.created(URI.create("account/" + account.getName())).build();
    }

    @POST
    @Path(value = "/{account}")
    public Response modifyAccount(String wallet, Account account) {
        Account currentAccount = findAccount(wallet);
        throwConflictExceptionIfAccountAlreadyExists(account);
        currentAccount.setName(account.getName());
        return Response.ok().build();
    }

    @DELETE
    @Path(value = "/{account}")
    public Response removeAccount(String WALLET) {
        Account account = findAccount(WALLET);
        throwBadRequestExceptionIfAccountHasTransactions(account);
        getUser().removeAccount(account);
        return Response.ok().build();
    }

    @GET
    public List<Account> getAccounts(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(200);
        return getUser().getAccounts();
    }

    /**
     * Checks if given account has transactions and if true throws
     * {@link javax.ws.rs.BadRequestException}
     *
     * @param account the account to check
     * @throws BadRequestException when the account has transactions
     */
    private void throwBadRequestExceptionIfAccountHasTransactions(Account account) throws
            BadRequestException {
        if (account.hasTransactions()) {
            throw new BadRequestException(String.format(ACCOUNT_HAS_TRANSACTIONS, account.getName()));
        }
    }

    /**
     * Checks if an account with the same name as given account already exists and if true throws
     * {@link pl.foltak.mybudget.server.rest.exception.ConflictException}.
     *
     * @param account account to check
     * @throws ConflictException when the account with the same name already exists
     */
    private void throwConflictExceptionIfAccountAlreadyExists(Account account) throws
            ConflictException {
        if (getUser().findAccount(account.getName()).isPresent()) {
            throw new ConflictException(String.format(ACCOUNT_ALREADY_EXISTS, account.getName()));
        }
    }
}
