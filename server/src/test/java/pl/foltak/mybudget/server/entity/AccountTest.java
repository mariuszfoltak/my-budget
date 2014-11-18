package pl.foltak.mybudget.server.entity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test of Account entity.
 *
 * @author mariusz@foltak.pl
 */
public class AccountTest {

    private Account instance;

    @Before
    public void setUp() {
        instance = new Account();
    }

    /**
     * When transaction list is empty, then method hasTransactions() should return false.
     */
    @Test
    public void isMethodHasTransactionReturnFalseWhenTransactionListIsEmpty() {
        instance.transactions = new LinkedList<>();
        assertFalse("Method should return false", instance.hasTransactions());
    }

    /**
     * When the transaction list has elements, then the method hasTransactions() should return true.
     */
    @Test
    public void isMethodHasTransactionReturnTrueWhenTransactionListHasElements() {
        instance.transactions = Arrays.asList(mock(Transaction.class));
        assertTrue("Method hasTransactions() should return true", instance.hasTransactions());
    }

    /**
     * When addTransaction() is called, then the method should add a transaction to the list.
     */
    @Test
    public void isTransactionAddedToListWhenAddTransactionIsCalled() {
        final Transaction transaction = mock(Transaction.class);
        instance.transactions = mock(List.class);
        instance.addTransaction(transaction);
        verify(instance.transactions).add(transaction);
    }

    /**
     * When addTransaction() is called with null parameter, then method should throw
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void isIllegalArgumentExceptionThrownWhenAddTransactionMethodWithNullParameterIsCalled() {
        instance.addTransaction(null);
    }

    /**
     * When removeTransaction() is called, then the method should remove a transaction from the
     * list.
     */
    @Test
    public void isTransactionRemovedFromListWhenRemoveTransactionIsCalled() {
        final Transaction transaction = mock(Transaction.class);
        instance.transactions = mock(List.class);
        instance.removeTransaction(transaction);
        verify(instance.transactions).remove(transaction);
    }

    /**
     * When findTransaction() is called, then the method should return a transaction with given name
     * from list.
     */
    @Test
    public void isTransactionReturnedWhenFindTransactionIsCalled() {
        final long id = 47L;
        final Transaction transaction = mock(Transaction.class);
        when(transaction.getId()).thenReturn(id);
        instance.transactions = Arrays.asList(transaction);

        assertSame("Wrong object returned", transaction, instance.findTransaction(id).get());
    }

    /**
     * When findTransaction() is called but any transaction with given name exists, then the method
     * should return an empty Optional.
     */
    @Test
    public void isEmptyOptionalReturnedWhenFindTransactionIsCalledAndTransactionDoesntExist() {
        instance.transactions = new LinkedList<>();
        assertFalse("Optional must be empty", instance.findTransaction(13L).isPresent());
    }
}
