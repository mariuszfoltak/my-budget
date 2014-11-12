package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.User;
import static pl.foltak.mybudget.server.test.TestUtils.expectedException;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class AccountServiceTest {

    private static final String WALLET = "wallet";
    private static final String BANK = "bank";

    private AccountService instance;
    private User user;
    private Account walletAccount;
    private Account bankAccount;

    @Before
    public void setUp() {
        instance = new AccountService();
        instance.user = user = mock(User.class);

        walletAccount = mock(Account.class);
        when(walletAccount.getName()).thenReturn(WALLET);
        when(user.findAccount(WALLET)).thenReturn(walletAccount);

        bankAccount = mock(Account.class);
        when(bankAccount.getName()).thenReturn(BANK);
    }

    /**
     * When create account was called, service should return 201 Created
     */
    @Test
    public void isOkStatusReturnedWhenAccountIsCreated() {
        Response response = instance.createAccount(bankAccount);
        assertEquals("Status code isn't equal to 201 Created", 201, response.getStatus());
    }

    /**
     * When we call create account, service should return location header
     */
    @Test
    public void isLocationHeaderReturnedWhenAccountIsCreated() {
        Response response = instance.createAccount(bankAccount);
        assertEquals("Location header isn't correct", URI.create("account/bank"), response.getLocation());
    }

    /**
     * When create account is called, service should add account to user
     */
    @Test
    public void isServiceAddAccountToUserWhenAccountIsCreated() {
        instance.createAccount(bankAccount);
        verify(user, times(1)).addAccount(bankAccount);
    }

    /**
     * When create account is called and the account already exist, service should return 409 Conflict and doesn't add account.
     */
    @Test
    public void isConflictExceptionThrownWhenTryToCreateAccountThatAlreadyExists() {
        try {
            instance.createAccount(walletAccount);
            expectedException(ConflictException.class);
        } catch (Exception e) {
            verify(user, never()).addAccount(any());
        }
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
     */
    @Test
    public void isEntityUpdatedWhenAccountIsModified() {
        instance.modifyAccount(WALLET, bankAccount);
        verify(walletAccount).setName(BANK);
    }

    /**
     * When modify account is called and the account doesn't exist, service should return 404 Not Found.
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenTryToModifyAccountThatDoesntExist() {
        instance.modifyAccount(BANK, walletAccount);
    }

    /**
     * When modify account is called and an account with new name already exist, service should return 409 Conflict and doesn't modify account.
     */
    @Test
    public void isConflictExceptionThrownWhenTryToModifyAccountToExistingAccount() {
        try {
            instance.modifyAccount(WALLET, walletAccount);
            expectedException(ConflictException.class);
        } catch (ConflictException e) {
            verify(walletAccount, never()).setName(any());
        }
    }

    /**
     * When remove account is called, service should return 200 OK.
     */
    @Test
    public void isOkStatusReturnedWhenAccountIsRemoved() {
        Response response = instance.removeAccount(WALLET);
        assertEquals("Incorrect status code", 200, response.getStatus());
    }

    /**
     * When remove account is called, service should remove entity.
     */
    @Test
    public void isEntityRemovedWhenAccountIsRemoved() {
        instance.removeAccount(WALLET);
        verify(user).removeAccount(walletAccount);
    }

    /**
     * When remove account is called and account doesn't exist, service should return 404 Not Found.
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenTryToRemoveAccountThatDoesntExist() {
        instance.removeAccount(BANK);
    }

    /**
     * When remove account is called and the account has transactions,
     * service should return 400 Bad Request.
     */
    @Test
    public void isBadRequestExceptionThrownWhenTryToRemoveAccountThatHasTransactions() {
        when(walletAccount.hasTransactions()).thenReturn(Boolean.TRUE);
        try {
            instance.removeAccount(WALLET);
            expectedException(BadRequestException.class);
        } catch (Exception e) {
            verify(user, never()).removeAccount(any());
        }
    }
    
    /**
     * When get accounts is called, service should return 200 OK.
     */
    @Test
    public void isOkStatusReturnedWhenGetAccountsIsCalled() {
        final HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        instance.getAccounts(httpServletResponse);
        verify(httpServletResponse).setStatus(200);
    }
    
    /**
     * When get accounts is called, service should return list of accounts.
     */
    @Test
    public void doesServiceReturnListOfAccountsWhenGetAccountsIsCalled() {
        List<Account> accounts = new LinkedList<>();
        accounts.add(bankAccount);
        accounts.add(walletAccount);
        when(user.getAccounts()).thenReturn(accounts);
        List<Account> result = instance.getAccounts(mock(HttpServletResponse.class));
        assertEquals("Incorrect account list", accounts, result);
    }
}
