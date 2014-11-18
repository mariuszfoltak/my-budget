package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.not;
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

    private static final long ID_47 = 47L;
    private static final String WALLET = "wallet";
    private static final String CANDY = "candy";
    private static final String FOOD = "food";
    private static final String NONEXISTENT = "nonexistent";
    private static final String FIRST_TAG = "firstTag";
    private static final String SECOND_TAG = "secondTag";

    private User user;
    private Account account;
    private Category subCategory;
    private Category mainCategory;
    private TransactionService instance;
    private TransactionDTO transactionDTO;

    private Tag firstTag;
    private Tag secondTag;
    private List<String> tags;
    private Transaction transaction;

    @Before
    public void setUp() {
        user = mock(User.class);
        tags = Arrays.asList(new String[]{FIRST_TAG, SECOND_TAG});
        account = mock(Account.class);
        instance = spy(new TransactionService());
        firstTag = mock(Tag.class);
        secondTag = mock(Tag.class);
        subCategory = mock(Category.class);
        transaction = mock(Transaction.class);
        mainCategory = mock(Category.class);
        transactionDTO = mock(TransactionDTO.class);

        doReturn(user).when(instance).getUser();
        doReturn(firstTag).when(instance).findOrCreateTag(FIRST_TAG);
        doReturn(secondTag).when(instance).findOrCreateTag(SECOND_TAG);
        doReturn(transaction).when(instance).convert(transactionDTO);
        doReturn(transaction).when(instance).updateTransaction(transactionDTO, transaction);
        
        when(user.findAccount(WALLET)).thenReturn(Optional.of(account));
        when(user.findAccount(NONEXISTENT)).thenReturn(Optional.ofNullable(null));
        when(user.findCategory(FOOD)).thenReturn(Optional.of(mainCategory));
        when(user.findCategory(NONEXISTENT)).thenReturn(Optional.ofNullable(null));
        when(account.findTransaction(ID_47)).thenReturn(Optional.of(transaction));
        when(account.findTransaction(not(eq(ID_47)))).thenReturn(Optional.ofNullable(null));
        when(mainCategory.findCategory(CANDY)).thenReturn(subCategory);
        when(transactionDTO.getId()).thenReturn(ID_47);
        when(transactionDTO.getCategoryPath()).thenReturn(FOOD + "/" + CANDY);
        when(transactionDTO.getTags()).thenReturn(tags);
    }

    /**
     * When create transaction is called, then service should return 201 Created. status.
     */
    @Test
    public void isCreatedStatusReturnedWhenCreateTransactionIsCalled() {
        Response response = instance.createTransaction(WALLET, transactionDTO);
        assertEquals("Incorrect status code", 201, response.getStatus());
    }

    /**
     * When create transaction is called, then service should return location header.
     */
    @Test
    public void isLocationHeaderReturnedWhenCreateTransactionIsCalled() {
        when(transaction.getId()).thenReturn(ID_47);
        Response response = instance.createTransaction(WALLET, transactionDTO);
        assertEquals("Incorrect location header", URI.create("transactions/wallet/47"),
                response.getLocation());
    }

    /**
     * When create transaction is called, then service should add entity to account.
     */
    @Test
    public void isEntityAddedToAccountWhenCreateTransactionIsCalled() {
        instance.createTransaction(WALLET, transactionDTO);
        verify(account).addTransaction(transaction);
    }

    /**
     * When create transaction is called, then service should add entity to category.
     */
    @Test
    public void isEntityAddedToCategoryWhenCreateTransactionIsCalled() {
        instance.createTransaction(WALLET, transactionDTO);
        verify(subCategory).addTransaction(transaction);
    }

    /**
     * When create transaction is called but account doesn't exist, then service should return 404
     * Not Found status.
     */
    @Test
    public void isNotFoundExceptionThrownWhenCreateTransactionIsCalledAndAccountDoesntExist() {
        try {
            instance.createTransaction(NONEXISTENT, transactionDTO);
            expectedException(NotFoundException.class);
        } catch (NotFoundException e) {
            verify(subCategory, never()).addTransaction(any());
        }
    }

    /**
     * When create transaction is called but main category doesn't exist, then service should return
     * 404 Not Found status.
     */
    @Test
    public void isNotFoundExceptionThrownWhenCreateTransactionIsCalledButMainCategoryDoesntExist() {
        try {
            when(transactionDTO.getCategoryPath()).thenReturn(NONEXISTENT + "/" + CANDY);
            instance.createTransaction(WALLET, transactionDTO);
            expectedException(NotFoundException.class);
        } catch (NotFoundException e) {
            verify(account, never()).addTransaction(any());
        }
    }

    /**
     * When create transaction is called but sub category doesn't exist, then service should return
     * 404 Not Found status.
     */
    @Test
    public void isNotFoundExceptionThrownWhenCreateTransactionIsCalledButSubCategoryDoesntExist() {
        try {
            when(transactionDTO.getCategoryPath()).thenReturn(FOOD + "/" + NONEXISTENT);
            instance.createTransaction(WALLET, transactionDTO);
            expectedException(NotFoundException.class);
        } catch (NotFoundException e) {
            verify(account, never()).addTransaction(any());
        }
    }

    /**
     * When create transaction is called, then service should find tags in user and add them to the
     * transaction that is created.
     */
    @Test
    public void areTagsAddedToTransactionWhenCreateTransactionIsCalled() {
        instance.createTransaction(WALLET, transactionDTO);

        verify(transaction).addTag(firstTag);
        verify(transaction).addTag(secondTag);
    }

    /**
     * When transaction is modified, then service should return 200 OK status.
     */
    @Test
    public void isOKStatusReturnedWhenModifyTransactionIsCalled() {
        Response response = instance.modifyTransaction(WALLET, transactionDTO);
        assertEquals(WALLET, 200, response.getStatus());
    }

    /**
     * When modify transaction is called, then service should update entity.
     */
    @Test
    public void isUpdateTransactionMethodCalledwhenModifyTransaction() {
        instance.modifyTransaction(WALLET, transactionDTO);
        verify(instance).updateTransaction(transactionDTO, transaction);
    }

    /**
     * When modify transaction is called but the account doesn't exist, then service should return
     * 404 Not Found status.
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenModifyTransactionIsCalledButAccountDoesntExist() {
        instance.modifyTransaction(NONEXISTENT, transactionDTO);
    }

    /**
     * When modify transaction is called but the transaction doesn't exist, then service should
     * return 404 Not Found status.
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenModifyTransactionIsCalledButTransactionDoesntExist() {
        instance.modifyTransaction(WALLET, mock(TransactionDTO.class));
    }

    /**
     * When transaction is modified, then transaction should be added to the requested transaction.
     */
    @Test
    public void isTransactionAddedToCategoryWhenModifyTransactionIsCalled() {
        instance.modifyTransaction(WALLET, transactionDTO);
        verify(subCategory).addTransaction(transaction);
    }

    /**
     * When modify transaction is called but the requested main category doesn't exist, then service
     * should return 404 Not Found status.
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenModifyTransactionIsCalledButRequestedMainCategoryDoesntExists() {
        when(transactionDTO.getCategoryPath()).thenReturn("nonexistent/" + CANDY);
        instance.modifyTransaction(WALLET, transactionDTO);
    }

    /**
     * When modify transaction is called but the requested sub category doesn't exist, then service
     * should return 404 Not Found status.
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenModifyTransactionIsCalledButRequestedSubCategoryDoesntExists() {
        when(transactionDTO.getCategoryPath()).thenReturn(FOOD + "/nonexistent");
        instance.modifyTransaction(WALLET, transactionDTO);
    }

    /**
     * When a transaction is modified, then all old tags should be removed from the transaction.
     */
    @Test
    public void areAllOldTagsRemovedFromTransactionWhenItIsModified() {
        when(transaction.getTags()).thenReturn(mock(List.class));
        instance.modifyTransaction(WALLET, transactionDTO);
        verify(transaction.getTags()).clear();
    }

/**
     * When a transaction is modified, then all new tags should be added to the transaction.
     */
    @Test
    public void areAllNewTagsAddedToTransactionWhenItIsModified() {
        when(transaction.getTags()).thenReturn(mock(List.class));
        instance.modifyTransaction(WALLET, transactionDTO);

        verify(transaction).addTag(firstTag);
        verify(transaction).addTag(secondTag);
    }

//    TODO: Validate transactionDTO (hasCategoryPath, hasDescription, etc)

    /**
     * When transaction is removed, then service should return 200 OK status.
     */
    @Test
    public void isOkStatusReturnedWhenRemoveTrasactionIsCalled() {
        Response response = instance.removeTransaction(WALLET, ID_47);
        assertEquals("Incorrect status code", 200, response.getStatus());
    }

    /**
     * When transaction is removed, then service should remove entity from an account.
     */
    @Test
    public void isEntityRemovedFromAccountWhenRemoveTransactionIsCalled() {
        instance.removeTransaction(WALLET, ID_47);
        verify(account).removeTransaction(transaction);
    }

    /**
     * When remove transaction is called but the account doesn't exist, service should return 404
     * Not Found.
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenRemoveTransactionIsCalledButAccountDoesntExist() {
        instance.removeTransaction(NONEXISTENT, ID_47);
    }

    /**
     * When remove transaction is called but the transaction doesn't exist, service should return
     * 404 Not Found.
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenRemoveTransactionIsCalledButTransactionDoesntExist() {
        instance.removeTransaction(WALLET, 13L);
    }
    
//    TODO: Get transactions by filters
    
}
