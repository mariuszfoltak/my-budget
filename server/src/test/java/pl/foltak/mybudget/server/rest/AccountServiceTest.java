package pl.foltak.mybudget.server.rest;

import java.net.URI;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.User;
import static pl.foltak.mybudget.server.rest.TestUtils.expectedException;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz.foltak@coi.gov.pl>
 */
public class AccountServiceTest {

    private AccountService instance;
    private User user;

    @Before
    public void setUp() {
        instance = new AccountService();
        instance.user = user = mock(User.class);
    }

    /**
     * When we call create account, service should return 201 Created
     */
    @Test
    public void testReturnOkStatusWhenCallingCreateAccount() {
        Response response = instance.createAccount(mock(Account.class));
        assertEquals("Status code isn't equal to 201 Created", 201, response.getStatus());
    }

    /**
     * When we call create account, service should return location header
     */
    @Test
    public void testReturnLocationHeaderWhenCallingCreateAccount() {
        final Account account = mock(Account.class);
        when(account.getName()).thenReturn("wallet");
        Response response = instance.createAccount(account);
        assertEquals("Location header isn't correct", URI.create("account/wallet"), response.getLocation());
    }

    /**
     * When we call create account, service should add account to user
     */
    @Test
    public void testServiceAddAccountToUserWhenCallingCreateAccount() {
        final Account account = mock(Account.class);
        instance.createAccount(account);
        verify(user, times(1)).addAccount(account);
    }

    /**
     * TODO: When we call create account and the account already exist, service should return 409 Conflict and doesn't add account
     */
    @Test
    public void testThrowConflictExceptionWhenCallingCreateAccountAndAccountAlreadyExists() {
        final Account account = mock(Account.class);
        when(account.getName()).thenReturn("wallet");
        when(user.findAccount("wallet")).thenReturn(account);
        try {
            instance.createAccount(account);
            expectedException(ConflictException.class);
        } catch (Exception e) {
            verify(user, never()).addAccount(any());
        }
    }

//    TODO: When we call modify account, service should return 200 OK
//    TODO: When we call modify account, service should modify account
//    TODO: When we call modify account and the account doesn't exist, service should retun 404 Not Found
//    TODO: When we call modify account and an account with new name already exist, service should return 409 Conflict and doesn't modify account
//    TODO: When we call remove account, service should return 200 OK
//    TODO: When we call remove account, service should remove account from user
//    TODO: When we call remove account and the account doesn't exist, service should return 404 Not Found
//    TODO: When we call remove account and the account has transactions, service should return 400 Bad Request
}
