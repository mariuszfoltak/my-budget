package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.dao.exception.AccountAlreadyExistsException;
import pl.foltak.mybudget.server.dao.exception.AccountCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.AccountNotFoundException;
import pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException;
import pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException;
import pl.foltak.mybudget.server.dto.TransactionDTO;
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

    /**
     * Creates new account.
     *
     * @param account The account that should be created
     * @return 201 Created when the account was created or 409 Conflict when the account already
     * exists
     */
    @PUT
    @Path("/")
    public Response createAccount(Account account) {
        try {
            getDao().addAccount(getUsername(), account);
        } catch (AccountAlreadyExistsException ex) {
            throw new ConflictException(ex.getMessage(), ex);
        }
        // TODO: stworzyć uriCreator
        return Response.created(URI.create("account/" + account.getName())).build();
    }

    /**
     * Modifies an account in user .
     *
     * @param accountName the name of the account that should be modified
     * @param account new account data
     * @return 200 OK when an account was modified, 404 Not Found when the account doesn't exist and
     * 409 Conflict when an account with new name already exists
     */
    @POST
    @Path("/{account}")
    public Response modifyAccount(@PathParam("account") String accountName, Account account) {
        try {
            getDao().updateAccount(getUsername(), accountName, account);
        } catch (AccountAlreadyExistsException ex) {
            throw new ConflictException(ex.getMessage(), ex);
        } catch (AccountNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        }
        return Response.ok().build();
    }

    /**
     * Removes an account from user accounts.
     *
     * @param accountName the name of account that should be removed.
     * @return 200 OK when the account was removed or 404 Not Found when the account doesn't exist.
     */
    @DELETE
    @Path("/{account}")
    public Response removeAccount(@PathParam("account") String accountName) {
        try {
            getDao().removeAccount(getUsername(), accountName);
        } catch (AccountNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        } catch (AccountCantBeRemovedException ex) {
            throw new BadRequestException(ex.getMessage(), ex);
        }
        return Response.ok().build();
    }

    /**
     * Returns list of accounts belongs to the user.
     *
     * @return list of accounts
     */
    @GET
    @Path("/")
    public Response getAccounts() {
        final List<Account> accounts = getDao().getAccounts(getUsername());
        return Response.ok(accounts).build();
    }

    @PUT
    @Path("/{account}")
    public Response createTransaction(@PathParam("account") String accountName,
            TransactionDTO transactionDTO) {

        try {
            getDao().addTransaction(getUsername(), transactionDTO);
        } catch (AccountNotFoundException | CategoryNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        }
        // TODO: create uriCreator
        return Response.created(URI.create("transaction/" + transactionDTO.getId())).build();
    }

    @POST
    @Path("/{account}")
    public Response modifyTransaction(@PathParam("account") String accountName,
            TransactionDTO transactionDTO) {

        try {
            getDao().updateTransaction(getUsername(), transactionDTO);
        } catch (AccountNotFoundException | TransactionNotFoundException | CategoryNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/")
    public Response removeTransaction(@PathParam("account") String accountName, long transactionId) {
        try {
            getDao().removeTransaction(getUsername(), transactionId);
        } catch (TransactionNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        }
        return Response.ok().build();
    }
}
