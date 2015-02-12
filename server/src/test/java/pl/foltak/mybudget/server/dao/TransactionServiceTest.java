package pl.foltak.mybudget.server.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
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
    @Spy
    private MyBudgetDao instance;
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
        instance = spy(new MyBudgetDao());
        firstTag = mock(Tag.class);
        secondTag = mock(Tag.class);
        subCategory = mock(Category.class);
        transaction = mock(Transaction.class);
        mainCategory = mock(Category.class);
        transactionDTO = mock(TransactionDTO.class);

        doReturn(user).when(instance).getUserByName(USERNAME);
        doReturn(firstTag).when(instance).findOrCreateTag(FIRST_TAG);
        doReturn(secondTag).when(instance).findOrCreateTag(SECOND_TAG);
        doReturn(transaction).when(instance).convertTransaction(transactionDTO);
        doNothing().when(instance).updateTransaction(transaction, transactionDTO);

        when(user.findAccount(any())).thenReturn(Optional.ofNullable(null));
        when(user.findAccount(WALLET)).thenReturn(Optional.of(account));
        when(user.findCategory(any())).thenReturn(Optional.ofNullable(null));
        when(user.findCategory(FOOD)).thenReturn(Optional.of(mainCategory));
        when(user.findTransaction(anyLong())).thenReturn(Optional.ofNullable(null));
        when(user.findTransaction(ID_47)).thenReturn(Optional.of(transaction));
        when(mainCategory.findSubCategory(any())).thenReturn(Optional.ofNullable(null));
        when(mainCategory.findSubCategory(CANDY)).thenReturn(Optional.of(subCategory));
        when(transactionDTO.getId()).thenReturn(ID_47);
        when(transactionDTO.getMainCategoryName()).thenReturn(FOOD);
        when(transactionDTO.getSubCategoryName()).thenReturn(CANDY);
        when(transactionDTO.getTags()).thenReturn(tags);
        when(transactionDTO.getAccountName()).thenReturn(WALLET);
    }

    /**
     * When create transaction is called, then dao should add entity to account.
     *
     * @throws AccountNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test
    public void isEntityAddedToAccountWhenCreateTransactionIsCalled()
            throws AccountNotFoundException, CategoryNotFoundException {

        instance.addTransaction(USERNAME, transactionDTO);
        verify(account).addTransaction(transaction);
    }

    /**
     * When create transaction is called, then service should add entity to category.
     *
     * @throws AccountNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test
    public void isEntityAddedToCategoryWhenCreateTransactionIsCalled()
            throws AccountNotFoundException, CategoryNotFoundException {

        instance.addTransaction(USERNAME, transactionDTO);
        verify(subCategory).addTransaction(transaction);
    }

    /**
     * When create transaction is called but the account doesn't exist, then an exception should be
     * thrown.
     *
     * @throws AccountNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test(expected = AccountNotFoundException.class)
    public void isExceptionThrownWhenAddingTransactionToNonexistingAccount()
            throws AccountNotFoundException, CategoryNotFoundException {

        when(transactionDTO.getAccountName()).thenReturn(NONEXISTENT);
        instance.addTransaction(USERNAME, transactionDTO);
    }

    /**
     * When create transaction is called but main category doesn't exist, then
     * CategoryNotFoundException should be thrown.
     *
     * @throws pl.foltak.mybudget.server.dao.exception.AccountNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isExceptionThrownWhenAddingTransactionToNonexistingMainCategory()
            throws AccountNotFoundException, CategoryNotFoundException {

        when(transactionDTO.getMainCategoryName()).thenReturn(NONEXISTENT);
        when(transactionDTO.getSubCategoryName()).thenReturn(CANDY);
        instance.addTransaction(USERNAME, transactionDTO);
    }

    /**
     * When create transaction is called but sub category doesn't exist, then
     * CategoryNotFoundException should be thrown.
     *
     * @throws AccountNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isNotFoundExceptionThrownWhenCreateTransactionIsCalledButSubCategoryDoesntExist()
            throws AccountNotFoundException, CategoryNotFoundException {

        when(transactionDTO.getMainCategoryName()).thenReturn(FOOD);
        when(transactionDTO.getSubCategoryName()).thenReturn(NONEXISTENT);
        instance.addTransaction(USERNAME, transactionDTO);
    }

    /**
     * When create transaction is called, then MyBudgetDao should find tags in user and add them to
     * the transaction that is created.
     *
     * @throws AccountNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test
    public void areTagsAddedToTransactionWhenCreateTransactionIsCalled()
            throws AccountNotFoundException, CategoryNotFoundException {

        instance.addTransaction(USERNAME, transactionDTO);

        verify(transaction).addTag(firstTag);
        verify(transaction).addTag(secondTag);
    }

    /**
     * When modify transaction is called, then service should update entity.
     *
     * @throws AccountNotFoundException
     * @throws TransactionNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test
    public void isUpdateTransactionMethodCalledwhenModifyTransaction()
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {

        instance.updateTransaction(USERNAME, transactionDTO);
        verify(instance).updateTransaction(transaction, transactionDTO);
    }

    /**
     * AccountNotFoundException should be thrown, when updating transaction to nonexistent account.
     *
     * @throws AccountNotFoundException
     * @throws TransactionNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test(expected = AccountNotFoundException.class)
    public void isExceptionThrownWhenUpdatingTransactionToNonexistentAccount()
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {

        when(transactionDTO.getAccountName()).thenReturn(NONEXISTENT);
        instance.updateTransaction(USERNAME, transactionDTO);
    }

    /**
     * When modify transaction is called but the transaction doesn't exist, then service should
     * return 404 Not Found status.
     *
     * @throws AccountNotFoundException
     * @throws TransactionNotFoundException should be thrown
     * @throws CategoryNotFoundException
     */
    @Test(expected = TransactionNotFoundException.class)
    public void isExceptionThrownWhenUpdatingNonexistentTransaction()
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {
        when(transactionDTO.getId()).thenReturn(-1L);
        instance.updateTransaction(USERNAME, transactionDTO);
    }

    /**
     * When transaction is modified, then transaction should be added to the requested transaction.
     *
     * @throws AccountNotFoundException
     * @throws TransactionNotFoundException
     * @throws CategoryNotFoundException
     */
    @Test
    public void isTransactionAddedToCategoryWhenModifyTransactionIsCalled()
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {

        instance.updateTransaction(USERNAME, transactionDTO);
        verify(subCategory).addTransaction(transaction);
    }

    /**
     * If we try to move transaction to main category that doesn't exist, then
     * CategoryNotFoundException should be thrown.
     *
     * @throws pl.foltak.mybudget.server.dao.exception.AccountNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isCategoryNotFoundExceptionThrownWhenMainCategoryDoesntExistDuringUpdateTransaction()
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {

        when(transactionDTO.getMainCategoryName()).thenReturn("nonexistent");
        instance.updateTransaction(USERNAME, transactionDTO);
    }

    /**
     * If we try to move transaction to sub category that doesn't exist, then
     * CategoryNotFoundException should be thrown.
     * 
     * @throws pl.foltak.mybudget.server.dao.exception.AccountNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isNotFoundExceptionThrownWhenModifyTransactionIsCalledButRequestedSubCategoryDoesntExists()
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {

        when(transactionDTO.getSubCategoryName()).thenReturn("nonexistent");
        instance.updateTransaction(USERNAME, transactionDTO);
    }

    /**
     * When a transaction is modified, then all old tags should be removed from the transaction.
     * 
     * @throws pl.foltak.mybudget.server.dao.exception.AccountNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException
     */
    @Test
    public void areAllOldTagsRemovedFromTransactionWhenItIsModified() 
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {
        
        instance.updateTransaction(USERNAME, transactionDTO);
        verify(transaction).clearTags();
    }

    /**
     * When a transaction is modified, then all new tags should be added to the transaction.
     * 
     * @throws pl.foltak.mybudget.server.dao.exception.AccountNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException
     */
    @Test
    public void areAllNewTagsAddedToTransactionWhenItIsModified() 
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {
        
        when(transaction.getTags()).thenReturn(mock(List.class));
        instance.updateTransaction(USERNAME, transactionDTO);

        verify(transaction).addTag(firstTag);
        verify(transaction).addTag(secondTag);
    }

//    TODO: Validate transactionDTO (hasCategoryPath, hasDescription, etc)

    /**
     * When transaction is removed, then service should remove entity from an account.
     * 
     * @throws pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException
     */
    @Test
    public void isEntityRemovedFromAccountWhenRemoveTransactionIsCalled() 
            throws TransactionNotFoundException {
        instance.removeTransaction(USERNAME, ID_47);
        verify(user).removeTransaction(transaction);
    }

    /**
     * When remove transaction is called but the transaction doesn't exist, service should return
     * 404 Not Found.
     * 
     * @throws pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException
     */
    @Test(expected = TransactionNotFoundException.class)
    public void isNotFoundExceptionThrownWhenRemoveTransactionIsCalledButAccountDoesntExist() 
            throws TransactionNotFoundException {
        instance.removeTransaction(USERNAME, 404L);
    }
    
    @Test
    public void testConvertingTransactionDtoToTransactionEntity() {
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountName(WALLET);
        dto.setMainCategoryName(FOOD);
        dto.setSubCategoryName(CANDY);
        dto.setAmount(3.14);
        dto.setDescription("test");
        dto.setTransactionDate(new Date());
        
        Transaction entity = instance.convertTransaction(dto);
        assertThat(entity.getAmount(), is(dto.getAmount()));
        assertThat(entity.getDescription(), is(dto.getDescription()));
        assertThat(entity.getTransactionDate(), is(dto.getTransactionDate()));
    }

//    TODO: Get transactions by filters
}
