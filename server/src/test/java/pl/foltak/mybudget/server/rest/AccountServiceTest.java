package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.dao.MyBudgetDaoLocal;
import pl.foltak.mybudget.server.dao.exception.AccountAlreadyExistsException;
import pl.foltak.mybudget.server.dao.exception.AccountCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.AccountNotFoundException;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class AccountServiceTest {

    private static final String WALLET = "wallet";
    private static final String BANK = "bank";
    private static final String USERNAME = "alibaba";
    private static final String NONEXISTENT = "nonexistent";

    private AccountService instance;
    private Account walletAccount;
    private Account bankAccount;
    private MyBudgetDaoLocal dao;

    @Before
    public void setUp() {
        instance = spy(new AccountService());
        dao = mock(MyBudgetDaoLocal.class);
        
        walletAccount = mock(Account.class);
        bankAccount = mock(Account.class);

        doReturn(USERNAME).when(instance).getUsername();
        doReturn(dao).when(instance).getDao();
        
        when(bankAccount.getName()).thenReturn(BANK);
    }

    /**
     * When create account was called, service should return 201 Created.
     */
    @Test
    public void isOkStatusReturnedWhenAccountIsCreated() {
        Response response = instance.createAccount(bankAccount);
        assertEquals("Status code isn't equal to 201 Created", 201, response.getStatus());
    }

    /**
     * When we call create account, service should return location header.
     */
    @Test
    public void isLocationHeaderReturnedWhenAccountIsCreated() {
        Response response = instance.createAccount(bankAccount);
        assertEquals("Location header isn't correct", URI.create("accounts/bank"),
                response.getLocation());
    }

    /**
     * When create account is called, service should add account to user.
     * 
     * @throws AccountAlreadyExistsException
     */
    @Test
    public void isServiceAddAccountToUserWhenAccountIsCreated() 
            throws AccountAlreadyExistsException {
        instance.createAccount(bankAccount);
        verify(dao).addAccount(USERNAME, bankAccount);
    }

    /**
     * When create account is called and the account already exist, service should return 409
     * Conflict and doesn't add account.
     * 
     * @throws AccountAlreadyExistsException
     */
    @Test(expected = ConflictException.class)
    public void isConflictExceptionThrownWhenTryToCreateAccountThatAlreadyExists() 
            throws AccountAlreadyExistsException {
        doThrow(AccountAlreadyExistsException.class).when(dao)
                .addAccount(USERNAME, walletAccount);
        instance.createAccount(walletAccount);
    }

    /**
     * When modify account is called, service should return 200 OK.
     */
    @Test
    public void isOkStatusReturnedWhenAccountIsModified() {
        Response response = instance.modifyAccount(WALLET, bankAccount);
        assertEquals("Incorrect status code", 200, response.getStatus());
    }

    /**
     * When modify account is called, service should update account entity.
     * 
     * @throws AccountAlreadyExistsException
     * @throws AccountNotFoundException
     */
    @Test
    public void isEntityUpdatedWhenAccountIsModified()
            throws AccountAlreadyExistsException, AccountNotFoundException {
        
        instance.modifyAccount(WALLET, bankAccount);
        verify(dao).updateAccount(USERNAME, WALLET, bankAccount);
    }

    /**
     * When modify account is called and the account doesn't exist, service should return 404 Not
     * Found.
     * 
     * @throws AccountAlreadyExistsException
     * @throws AccountNotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenTryToModifyAccountThatDoesntExist() 
            throws AccountAlreadyExistsException, AccountNotFoundException {
        
        doThrow(AccountNotFoundException.class).when(dao)
                .updateAccount(USERNAME, NONEXISTENT, walletAccount);
        
        instance.modifyAccount(NONEXISTENT, walletAccount);
    }

    /**
     * When modify account is called and an account with new name already exist, service should
     * return 409 Conflict.
     * 
     * @throws AccountAlreadyExistsException
     * @throws AccountNotFoundException
     */
    @Test (expected = ConflictException.class)
    public void isConflictExceptionThrownWhenTryToModifyAccountToExistingAccount() 
            throws AccountAlreadyExistsException, AccountNotFoundException {
        
        doThrow(AccountAlreadyExistsException.class).when(dao)
                .updateAccount(USERNAME, WALLET, walletAccount);
        
        instance.modifyAccount(WALLET, walletAccount);
    }

    /**
     * When remove account is called, service should return 200 OK.
     */
    @Test
    public void isOkStatusReturnedWhenAccountIsRemoved() {
        Response response = instance.removeAccount(12L);
        assertEquals("Incorrect status code", 200, response.getStatus());
    }

    /**
     * When remove account is called, service should remove entity.
     * 
     * @throws AccountNotFoundException
     * @throws AccountCantBeRemovedException
     */
    @Test
    public void isEntityRemovedWhenAccountIsRemoved() 
            throws AccountNotFoundException, AccountCantBeRemovedException {
        
        instance.removeAccount(12L);
        verify(dao).removeAccount(USERNAME, 12L);
    }

    /**
     * When remove account is called and account doesn't exist, service should return 404 Not Found.
     * 
     * @throws AccountNotFoundException
     * @throws AccountCantBeRemovedException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenTryToRemoveAccountThatDoesntExist() 
            throws AccountNotFoundException, AccountCantBeRemovedException {
        
        doThrow(AccountNotFoundException.class).when(dao)
                .removeAccount(USERNAME, 13L);
        
        instance.removeAccount(13L);
    }

    /**
     * When remove account is called and the account has transactions, service should return 400 Bad
     * Request.
     * 
     * @throws AccountNotFoundException
     * @throws AccountCantBeRemovedException
     */
    @Test (expected = BadRequestException.class)
    public void isBadRequestExceptionThrownWhenTryToRemoveAccountThatHasTransactions() 
            throws AccountNotFoundException, AccountCantBeRemovedException {
        
        doThrow(AccountCantBeRemovedException.class).when(dao)
                .removeAccount(USERNAME, 12L);
        
        instance.removeAccount(12L);
    }

    /**
     * When get accounts is called, service should return 200 OK.
     */
    @Test
    public void isOkStatusReturnedWhenGetAccountsIsCalled() {
        Response response = instance.getAccounts();
        assertEquals("Incorrect status code", 200, response.getStatus());
    }

    /**
     * When get accounts is called, service should return list of accounts.
     */
    @Test
    public void doesServiceReturnListOfAccountsWhenGetAccountsIsCalled() {
        List<Account> accounts = new LinkedList<>();
        accounts.add(bankAccount);
        accounts.add(walletAccount);
        when(dao.getAccounts(USERNAME)).thenReturn(accounts);
        List<Account> result = (List<Account>) instance.getAccounts().getEntity();
        assertEquals("Incorrect account list", accounts, result);
    }
}
