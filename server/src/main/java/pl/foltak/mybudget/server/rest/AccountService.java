package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.List;
import javax.ejb.EJB;
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
import pl.foltak.mybudget.server.dao.MyBudgetDaoLocal;
import pl.foltak.mybudget.server.dao.exception.AccountAlreadyExistsException;
import pl.foltak.mybudget.server.dao.exception.AccountCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.AccountNotFoundException;
import pl.foltak.mybudget.server.dto.TransactionDTO;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.Tag;
import pl.foltak.mybudget.server.entity.Transaction;
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
    
    @EJB
    MyBudgetDaoLocal dao;

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
            dao.createAccount(getUsername(), account);
        } catch (AccountAlreadyExistsException ex) {
            throw new ConflictException(String.format(ACCOUNT_ALREADY_EXISTS, account.getName()), ex);
        }
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
            dao.updateAccount(getUsername(), accountName, account);
        } catch (AccountAlreadyExistsException ex) {
            throw new ConflictException(String.format(ACCOUNT_ALREADY_EXISTS, account.getName()), ex);
        } catch (AccountNotFoundException ex) {
            throw new NotFoundException(String.format(ACCOUNT_DOESNT_EXIST, accountName));
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
            dao.removeAccount(getUsername(), accountName);
        } catch (AccountNotFoundException ex) {
            throw new NotFoundException(String.format(ACCOUNT_DOESNT_EXIST, accountName));
        } catch (AccountCantBeRemovedException ex) {
            throw new BadRequestException(String.format(ACCOUNT_HAS_TRANSACTIONS, accountName));
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
        final List<Account> accounts = dao.getAccounts(getUsername());
        return Response.ok(accounts).build();
    }

    @PUT
    @Path("/{account}")
    public Response createTransaction(@PathParam("account") String accountName,
            TransactionDTO transactionDTO) {

        final Account account = findAccount(accountName);
        final Category subCategory = getTargetCategory(transactionDTO);
        final Transaction transaction = convert(transactionDTO);

        account.addTransaction(transaction);
        subCategory.addTransaction(transaction);

        addTagsToTransaction(transaction, transactionDTO);

        return Response.created(URICreator.of(accountName, transaction)).build();
    }

    @POST
    @Path("/{account}")
    public Response modifyTransaction(@PathParam("account") String accountName,
            TransactionDTO transactionDTO) {

        Transaction transaction = findTransaction(findAccount(accountName), transactionDTO.getId());
        transaction = updateTransaction(transactionDTO, transaction);
        getTargetCategory(transactionDTO).addTransaction(transaction);

        addTagsToTransaction(transaction, transactionDTO);

        return Response.ok().build();
    }

    @DELETE
    @Path("/")
    public Response removeTransaction(@PathParam("account") String accountName, long transactionId) {
        final Account account = findAccount(accountName);
        final Transaction transaction = findTransaction(account, transactionId);
        account.removeTransaction(transaction);
        return Response.ok().build();
    }

    private void addTagsToTransaction(Transaction transaction, TransactionDTO transactionDTO) {
        transaction.clearTags();
        for (String tagName : transactionDTO.getTags()) {
            transaction.addTag(findOrCreateTag(tagName));
        }
    }

    private Transaction findTransaction(Account account, long transactionId)
            throws NotFoundException {

        return account.findTransaction(transactionId).orElseThrow(
                () -> {
                    return new NotFoundException(String.format(
                                    "Transaction with id=%s doesn't exist", transactionId)
                    );
                });
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

    private Category getTargetCategory(TransactionDTO transactionDTO) throws NotFoundException {
        String categoryPath = transactionDTO.getCategoryPath();
        String[] categoriesNames = categoryPath.split("/");
        Category mainCategory = findMainCategory(categoriesNames[0]);
        Category subCategory = findSubCategory(mainCategory, categoriesNames[1]);
        return subCategory;
    }

    Transaction convert(TransactionDTO transactionDTO) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    Tag findOrCreateTag(String firstTagName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    Transaction updateTransaction(TransactionDTO transactionDTO,
            Transaction mock) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
