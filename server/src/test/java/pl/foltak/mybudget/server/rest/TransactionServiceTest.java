package pl.foltak.mybudget.server.rest;

import java.net.URI;
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
import pl.foltak.mybudget.server.entity.Transaction;

/**
 *
 * @author mfoltak
 */
public class TransactionServiceTest {

    private static final long ID_47 = 47L;
    private static final String WALLET = "wallet";
    private static final String USERNAME = "alibaba";
    private static final String NONEXISTENT = "nonexistent";

    private TransactionService instance;
    private TransactionDTO transactionDTO;

    private Transaction transaction;
    private MyBudgetDaoLocal dao;

    @Before
    public void setUp() {
        dao = mock(MyBudgetDaoLocal.class);
        instance = spy(new TransactionService());
        transaction = mock(Transaction.class);
        transactionDTO = mock(TransactionDTO.class);

        doReturn(dao).when(instance).getDao();
        doReturn(USERNAME).when(instance).getUsername();

        when(transactionDTO.getId()).thenReturn(ID_47);
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
        assertEquals("Incorrect location header", URI.create("transaction/47"),
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
        verify(dao).addTransaction(USERNAME, transactionDTO);
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
                .addTransaction(any(), any());
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
                .addTransaction(any(), any());
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
