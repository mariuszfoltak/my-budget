package pl.foltak.mybudget.server.dao;

import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import pl.foltak.mybudget.server.dao.exception.AccountAlreadyExistsException;
import pl.foltak.mybudget.server.dao.exception.AccountCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.AccountNotFoundException;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.User;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class MyBudgetDaoAccountTest {

    private static final String BANK = "Bank";
    private static final String WALLET = "wallet";
    private static final String USERNAME = "alibaba";
    private static final String NONEXISTENT = "nonexistent";

    private User user;
    private Account walletAccount;
    private Account bankAccount;
    private MyBudgetDao instance;

    @Before
    public void setUp() {
        instance = spy(new MyBudgetDao());
        user = mock(User.class);
        walletAccount = mock(Account.class);
        bankAccount = mock(Account.class);

        doReturn(user).when(instance).getUserByName(USERNAME);
        doNothing().when(instance).setAccountFields(any(), any());

        when(walletAccount.getName()).thenReturn(WALLET);
        when(bankAccount.getName()).thenReturn(BANK);

        when(user.findAccount(any())).thenReturn(Optional.ofNullable(null));
        when(user.findAccount(WALLET)).thenReturn(Optional.of(walletAccount));
        when(user.findAccount(BANK)).thenReturn(Optional.of(bankAccount));

        when(walletAccount.hasTransactions()).thenReturn(Boolean.TRUE);
    }

    /**
     * Method createAccount() should add an account to the user.
     *
     * @throws AccountAlreadyExistsException shouldn't be thrown
     */
    @Test
    public void isAccountAddedToTheUser() throws AccountAlreadyExistsException {

        final Account newAccount = mock(Account.class);

        instance.addAccount(USERNAME, newAccount);
        verify(user).addAccount(newAccount);
    }

    /**
     * Method createAccount() should throws exception when account with given name already exists.
     *
     * @throws AccountAlreadyExistsException should be thrown
     */
    @Test(expected = AccountAlreadyExistsException.class)
    public void isExceptionThrownWhenTryToCreateAccountThatAlreadyExists()
            throws AccountAlreadyExistsException {

        instance.addAccount(USERNAME, walletAccount);
    }

    /**
     * Method updateAccount should update the entity.
     *
     * @throws AccountAlreadyExistsException should never be thrown
     * @throws AccountNotFoundException should never be thrown
     */
    @Test
    public void isEntityUpdatedWhenModifyAccount()
            throws AccountAlreadyExistsException, AccountNotFoundException {

        final Account newAccount = mock(Account.class);

        instance.updateAccount(USERNAME, WALLET, newAccount);
        verify(instance).setAccountFields(walletAccount, newAccount);
    }

    /**
     * Method updateAccount should throws AccountNotFoundException when the account with given name
     * doesn't exist.
     *
     * @throws AccountAlreadyExistsException
     * @throws AccountNotFoundException
     */
    @Test(expected = AccountNotFoundException.class)
    public void isExceptionThrownWhenTryModifyAccountThatDoesntExist()
            throws AccountAlreadyExistsException, AccountNotFoundException {

        instance.updateAccount(USERNAME, NONEXISTENT, walletAccount);
    }

    /**
     * Method updateAccount should throws AccountAlreadyExistException when account with given name
     * already exists.
     *
     * @throws AccountAlreadyExistsException should be thrown
     * @throws AccountNotFoundException should never be thrown
     */
    @Test(expected = AccountAlreadyExistsException.class)
    public void isExceptionThrownWhenTryChangeAccountNameToNameWhichIsUsedByAnotherAccount()
            throws AccountAlreadyExistsException, AccountNotFoundException {

        final Account newAccount = mock(Account.class);
        when(newAccount.getName()).thenReturn(WALLET);

        instance.updateAccount(USERNAME, BANK, newAccount);
    }

    // TODO: 4. not throw exception when account name and new account name are equals
    /**
     * Method removeAccount should delete the account from user.
     *
     * @throws AccountNotFoundException should never be thrown
     * @throws AccountCantBeRemovedException should never be thrown
     */
    @Test
    public void isEntityDeletedWhenRemovingAccount()
            throws AccountNotFoundException, AccountCantBeRemovedException {

        instance.removeAccount(USERNAME, BANK);
        verify(user).removeAccount(bankAccount);
    }

    /**
     * Method removeAccount should throws an AccountNotFoundException when an account with given
     * name doesn't exist.
     *
     * @throws AccountNotFoundException
     * @throws AccountCantBeRemovedException
     */
    @Test(expected = AccountNotFoundException.class)
    public void isExceptionThrownWhenTryToDeleteAccountThatDoesntExist()
            throws AccountNotFoundException, AccountCantBeRemovedException {

        instance.removeAccount(USERNAME, NONEXISTENT);
    }

    /**
     * Method removeAccount should throws an AccountCantBeRemovedException when the account has
     * transactions.
     *
     * @throws AccountNotFoundException should never be thrown
     * @throws AccountCantBeRemovedException should be thrown
     */
    @Test(expected = AccountCantBeRemovedException.class)
    public void isExceptionThrownWhenAccountHasTransactions()
            throws AccountNotFoundException, AccountCantBeRemovedException {

        instance.removeAccount(USERNAME, WALLET);
    }

    /**
     * Method getAccounts should return list of user accounts.
     */
    @Test
    public void isAccountListReturned() {
        final List accounts = mock(List.class);
        when(user.getAccounts()).thenReturn(accounts);
        assertSame(accounts, instance.getAccounts(USERNAME));
    }
}
