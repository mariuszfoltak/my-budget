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
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.dao.MyBudgetDaoLocal;
import pl.foltak.mybudget.server.dao.exception.AccountNotFoundException;
import pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException;
import pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException;
import pl.foltak.mybudget.server.dto.TransactionDTO;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.Tag;
import pl.foltak.mybudget.server.entity.Transaction;
import pl.foltak.mybudget.server.entity.User;

/**
 *
 * @author mfoltak
 */
public class TransactionServiceTest {

    private static final long ID_47 = 47L;
    private static final String FOOD = "food";
    private static final String CANDY = "candy";
    private static final String WALLET = "wallet";
    private static final String USERNAME = "alibaba";
    private static final String FIRST_TAG = "firstTag";
    private static final String SECOND_TAG = "secondTag";
    private static final String NONEXISTENT = "nonexistent";

    private User user;
    private Account account;
    private Category subCategory;
    private Category mainCategory;
    private AccountService instance;
    private TransactionDTO transactionDTO;

    private Tag firstTag;
    private Tag secondTag;
    private Transaction transaction;
    private List<String> tags;
    private MyBudgetDaoLocal dao;

    @Before
    public void setUp() {
        dao = mock(MyBudgetDaoLocal.class);
        user = mock(User.class);
        tags = Arrays.asList(new String[]{FIRST_TAG, SECOND_TAG});
        account = mock(Account.class);
        instance = spy(new AccountService());
        firstTag = mock(Tag.class);
        secondTag = mock(Tag.class);
        subCategory = mock(Category.class);
        transaction = mock(Transaction.class);
        mainCategory = mock(Category.class);
        transactionDTO = mock(TransactionDTO.class);

        doReturn(dao).when(instance).getDao();
        doReturn(user).when(instance).getUser();
        doReturn(USERNAME).when(instance).getUsername();
        doReturn(firstTag).when(instance).findOrCreateTag(FIRST_TAG);
        doReturn(secondTag).when(instance).findOrCreateTag(SECOND_TAG);
        doReturn(transaction).when(instance).convert(transactionDTO);
        doReturn(transaction).when(instance).updateTransaction(transactionDTO, transaction);

        when(user.findAccount(any())).thenReturn(Optional.ofNullable(null));
        when(user.findAccount(WALLET)).thenReturn(Optional.of(account));
        when(user.findCategory(any())).thenReturn(Optional.ofNullable(null));
        when(user.findCategory(FOOD)).thenReturn(Optional.of(mainCategory));
        when(account.findTransaction(anyLong())).thenReturn(Optional.ofNullable(null));
        when(account.findTransaction(ID_47)).thenReturn(Optional.of(transaction));
        when(mainCategory.findSubCategory(any())).thenReturn(Optional.ofNullable(null));
        when(mainCategory.findSubCategory(CANDY)).thenReturn(Optional.of(subCategory));
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
        assertEquals("Incorrect location header", URI.create("accounts/wallet/47"),
                response.getLocation());
    }

    /**
     * Method createTransaction should call MyBudgetDao.
     *
     * @throws AccountNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test
    public void isDaoCalledWhenCreateTransactionIsCalled() 
            throws AccountNotFoundException, CategoryNotFoundException {
        
        instance.createTransaction(WALLET, transactionDTO);
        verify(dao).addTransaction(USERNAME, WALLET, transaction);
    }

    /**
     * When create transaction is called but account doesn't exist, then service should return 404
     * Not Found status.
     *
     * @throws AccountNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenCreateTransactionIsCalledAndAccountDoesntExist()
            throws AccountNotFoundException, CategoryNotFoundException {

        doThrow(AccountNotFoundException.class).when(dao)
                .addTransaction(any(), any(), any());
        instance.createTransaction(NONEXISTENT, transactionDTO);
    }

    /**
     * When create transaction is called but category doesn't exist, then service should return 404
     * Not Found status.
     * @throws AccountNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenCreateTransactionIsCalledButCategoryDoesntExist() 
            throws AccountNotFoundException, CategoryNotFoundException {
        
        doThrow(CategoryNotFoundException.class).when(dao)
                .addTransaction(any(), any(), any());
        instance.createTransaction(WALLET, transactionDTO);
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
     * When modify transaction is called, then service should call dao.
     * 
     * @throws AccountNotFoundException
     * @throws TransactionNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test
    public void isDaoCalledwhenModifyTransaction() 
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {
        instance.modifyTransaction(WALLET, transactionDTO);
        verify(dao).updateTransaction(USERNAME, transactionDTO);
    }

    /**
     * When modify transaction is called but the transaction doesn't exist, then service should
     * return 404 Not Found status.
     * 
     * @throws AccountNotFoundException
     * @throws TransactionNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenModifyTransactionIsCalledButTransactionDoesntExist() 
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {
        
        doThrow(TransactionNotFoundException.class).when(dao)
                .updateTransaction(any(), any());
        instance.modifyTransaction(WALLET, mock(TransactionDTO.class));
    }

    /**
     * When modify transaction is called but the requested category doesn't exist, then service
     * should return 404 Not Found status.
     * 
     * @throws AccountNotFoundException
     * @throws TransactionNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenModifyTransactionIsCalledButRequestedCategoryDoesntExist() 
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {
        
        doThrow(CategoryNotFoundException.class).when(dao)
                .updateTransaction(any(), any());
        instance.modifyTransaction(WALLET, transactionDTO);
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
     * When transaction is removed, then service should call dao.
     * 
     * @throws TransactionNotFoundException
     */
    @Test
    public void isEntityRemovedFromAccountWhenRemoveTransactionIsCalled() 
            throws TransactionNotFoundException {
        
        instance.removeTransaction(WALLET, ID_47);
        verify(dao).removeTransaction(USERNAME, ID_47);
    }

    /**
     * When remove transaction is called but the transaction doesn't exist, service should return
     * 404 Not Found.
     * 
     * @throws TransactionNotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenRemoveTransactionIsCalledButTransactionDoesntExist() 
            throws TransactionNotFoundException {
        
        doThrow(TransactionNotFoundException.class).when(dao)
                .removeTransaction(any(), anyLong());
        instance.removeTransaction(WALLET, 13L);
    }

//    TODO: Get transactions by filters
}
