package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.dto.TransactionDTO;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.Tag;
import pl.foltak.mybudget.server.entity.Transaction;
import pl.foltak.mybudget.server.entity.User;
import static pl.foltak.mybudget.server.test.TestUtils.expectedException;

/**
 *
 * @author mfoltak
 */
public class TransactionServiceTest {

    private static final String WALLET = "wallet";
    private static final String CANDY = "candy";
    private static final String FOOD = "food";

    private TransactionService instance;
    private TransactionDTO transactionDTO;
    private Account account;
    private User user;
    private Category subCategory;
    private Category mainCategory;

    @Before
    public void setUp() {
        instance = spy(new TransactionService());
        instance.user = user = mock(User.class);
        account = mock(Account.class);
        transactionDTO = mock(TransactionDTO.class);
        mainCategory = mock(Category.class);
        subCategory = mock(Category.class);
        transaction = mock(Transaction.class);

        doReturn(transaction).when(instance).convert(transactionDTO);
        when(user.findAccount(WALLET)).thenReturn(account);
        when(user.findCategory(FOOD)).thenReturn(mainCategory);
        when(mainCategory.findCategory(CANDY)).thenReturn(subCategory);
    }
    private Transaction transaction;

    /**
     * When create transaction is called, then service should return 201
     * Created. status.
     */
    @Test
    public void isCreatedStatusReturnedWhenCreateTransactionIsCalled() {
        Response response = instance.createTransaction(WALLET, FOOD, CANDY, transactionDTO);
        assertEquals("Incorrect status code", 201, response.getStatus());
    }

    /**
     * When create transaction is called, then service should return location
     * header.
     */
    @Test
    public void isLocationHeaderReturnedWhenCreateTransactionIsCalled() {
        when(transaction.getId()).thenReturn(47L);
        Response response = instance.createTransaction(WALLET, FOOD, CANDY, transactionDTO);
        assertEquals("Incorrect location header", URI.create("transactions/wallet/47"), response.getLocation());
    }

    /**
     * When create transaction is called, then service should add entity to
     * account.
     */
    @Test
    public void isEntityAddedToAccountWhenCreateTransactionIsCalled() {
        instance.createTransaction(WALLET, FOOD, CANDY, transactionDTO);
        verify(account).addTransaction(transaction);
    }

    /**
     * When create transaction is called, then service should add entity to
     * category.
     */
    @Test
    public void isEntityAddedToCategoryWhenCreateTransactionIsCalled() {
        instance.createTransaction(WALLET, FOOD, CANDY, transactionDTO);
        verify(subCategory).addTransaction(transaction);
    }

    /**
     * When create transaction is called but account doesn't exist, then service
     * should return 404 Not Found status.
     */
    @Test
    public void isNotFoundExceptionThrownWhenCreateTransactionIsCalledAndAccountDoesntExist() {
        try {
            instance.createTransaction("nonexistent", FOOD, CANDY, transactionDTO);
            expectedException(NotFoundException.class);
        } catch (NotFoundException e) {
            verify(subCategory, never()).addTransaction(any());
        }
    }

    /**
     * When create transaction is called but main category doesn't exist, then
     * service should return 404 Not Found status.
     */
    @Test
    public void isNotFoundExceptionThrownWhenCreateTransactionIsCalledButMainCategoryDoesntExist() {
        try {
            instance.createTransaction(WALLET, "nonexistent", CANDY, transactionDTO);
            expectedException(NotFoundException.class);
        } catch (NotFoundException e) {
            verify(account, never()).addTransaction(any());
        }
    }

    /**
     * When create transaction is called but sub category doesn't exist, then
     * service should return 404 Not Found status.
     */
    @Test
    public void isNotFoundExceptionThrownWhenCreateTransactionIsCalledButSubCategoryDoesntExist() {
        try {
            instance.createTransaction(WALLET, FOOD, "nonexistent", transactionDTO);
            expectedException(NotFoundException.class);
        } catch (NotFoundException e) {
            verify(account, never()).addTransaction(any());
        }
    }

    /**
     * When create transaction is called, then service should find tags in user
     * and add them to the transaction that is created.
     */
    @Test
    public void areTagsAddedToTransactionWhenCreateTransactionIsCalled() {
        final String firstTagName = "firstTag";
        final String secondTagName = "secondTag";
        final Tag firstTag = mock(Tag.class);
        final Tag secondTag = mock(Tag.class);
        final List<String> tags = new LinkedList<>();

        tags.add(firstTagName);
        tags.add(secondTagName);

        when(transactionDTO.getTags()).thenReturn(tags);
        doReturn(firstTag).when(instance).findOrCreateTag(firstTagName);
        doReturn(secondTag).when(instance).findOrCreateTag(secondTagName);
        instance.createTransaction(WALLET, FOOD, CANDY, transactionDTO);

        verify(transaction).addTag(firstTag);
        verify(transaction).addTag(secondTag);
    }

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
}
