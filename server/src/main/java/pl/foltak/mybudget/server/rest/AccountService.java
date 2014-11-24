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

    @PUT
    @Path("/")
    public Response createAccount(Account account) {
        throwConflictExceptionIfAccountAlreadyExists(account);
        getUser().addAccount(account);
        return Response.created(URI.create("account/" + account.getName())).build();
    }

    @POST
    @Path("/{account}")
    public Response modifyAccount(@PathParam("account") String accountName, Account account) {
        Account currentAccount = findAccount(accountName);
        throwConflictExceptionIfAccountAlreadyExists(account);
        currentAccount.setName(account.getName());
        return Response.ok().build();
    }

    @DELETE
    @Path("/{account}")
    public Response removeAccount(@PathParam("account") String accountName) {
        Account account = findAccount(accountName);
        throwBadRequestExceptionIfAccountHasTransactions(account);
        getUser().removeAccount(account);
        return Response.ok().build();
    }

    @GET
    @Path("/")
    public Response getAccounts() {
        final List<Account> accounts = getUser().getAccounts();
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
