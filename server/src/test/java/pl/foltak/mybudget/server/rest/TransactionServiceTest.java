package pl.foltak.mybudget.server.rest;

import java.net.URI;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.Transaction;
import pl.foltak.mybudget.server.entity.User;

/**
 *
 * @author mfoltak
 */
public class TransactionServiceTest {

    private static final String WALLET = "wallet";
    private static final String CANDY = "candy";
    private static final String FOOD = "food";

    private TransactionService instance;
    private Transaction transaction;
    private Account account;
    private User user;
    private Category subCategory;
    private Category mainCategory;

    @Before
    public void setUp() {
        instance = new TransactionService();
        instance.user = user = mock(User.class);
        account = mock(Account.class);
        transaction = mock(Transaction.class);
        mainCategory = mock(Category.class);
        subCategory = mock(Category.class);

        when(user.findAccount(WALLET)).thenReturn(account);
        when(user.findCategory(FOOD)).thenReturn(mainCategory);
        when(mainCategory.findCategory(CANDY)).thenReturn(subCategory);
    }

    /**
     * When create transaction is called, then service should return 201
     * Created. status.
     */
    @Test
    public void isCreatedStatusReturnedWhenCreateTransactionIsCalled() {
        Response response = instance.createTransaction(WALLET, FOOD, CANDY, transaction);
        assertEquals("Incorrect status code", 201, response.getStatus());
    }

    /**
     * When create transaction is called, then service should return location
     * header.
     */
    @Test
    public void isLocationHeaderReturnedWhenCreateTransactionIsCalled() {
        when(transaction.getId()).thenReturn(47L);
        Response response = instance.createTransaction(WALLET, FOOD, CANDY, transaction);
        assertEquals("Incorrect location header", URI.create("transactions/wallet/47"), response.getLocation());
    }

    /**
     * When create transaction is called, then service should add entity to
     * category.
     */
    @Test
    public void isEntityAddedToAccountWhenCreateTransactionIsCalled() {
        instance.createTransaction(WALLET, FOOD, CANDY, transaction);
        verify(account).addTransaction(transaction);
    }

    @Test
    public void isEntityAddedToCategoryWhenCreateTransactionIsCalled() {
        instance.createTransaction(WALLET, FOOD, CANDY, transaction);
        verify(subCategory).addTransaction(transaction);
    }

//    TODO: When add transaction to sub category is called, then service should create tag
//    TODO: When add transaction to sub category is called, then service should use existing tag
//    TODO: When add transaction to sub category is called and main category doesn't exist, then service should return 404 Not Found
//    TODO: When add transaction to sub category is called and sub category doesn't exist, then service should return 404 Not Found
//    TODO: When add transaction to sub category is called and account doesn't exist, then service should return 404 Not Found
//    TODO: When add transaction to sub category is called and account doesn't exist, then service should return 404 Not Found
//    TODO: When modify transaction is called, then service should return 200 OK
//    TODO: When modify transaction is called, then service should modify entity
//    TODO: When modify transaction is called, then service should create tags
//    TODO: When modify transaction is called, then service should use existing tags
//    TODO: When modify transaction is called, then service should change category
//    TODO: When modify transaction is called and transaction doesn't exist, then service should return 404 Not Found
//    TODO: When modify transaction is called and main category doesn't exist, then service should return 404 Not Found
//    TODO: When modify transaction is called and sub category doesn't exist, then service should return 404 Not Found
//    TODO: When modify transaction is called and account doesn't exist, then service should return 404 Not Found
//    TODO: When remove transaction is called, then service should return 200 OK
//    TODO: When remove transaction is called, then service should remove entity from account
//    TODO: When remove transaction is called, then service should remove entity from category
//    TODO: When remove transaction is called and transaction doesn't exist, service should return 404 Not Found
//    TODO: When remove transaction is called and account doesn't exist, service should return 404 Not Found
//    TODO: Get transactions by filters
    private class test {}
}
