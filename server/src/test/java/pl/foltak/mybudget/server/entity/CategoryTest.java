package pl.foltak.mybudget.server.entity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Test of Category entity.
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class CategoryTest {

    private Category instance;

    @Before
    public void setUp() {
        instance = new Category();
        subCategory = mock(Category.class);
        when(subCategory.getName()).thenReturn(CANDY);
    }
    private static final String CANDY = "CANDY";

    /**
     * The addSubCategory() should add a subcategory to the list of subcategories.
     */
    @Test
    public void isCategoryAddedWhenAddCategoryIsCalled() {
        instance.subCategories = mock(List.class);
        instance.addSubCategory(subCategory);
        verify(instance.subCategories).add(subCategory);
    }

    /**
     * The addSubCategory() should throw NullPointerException when passed parameter is null.
     */
    @Test(expected = NullPointerException.class)
    public void isNullPointerExceptionThrownWhenAddSubCategoryWithNullParamIsCalled() {
        instance.subCategories = mock(List.class);
        instance.addSubCategory(null);
    }

    /**
     * The findSubCategory() should return subcategory.
     */
    @Test
    public void isSubCategoryReturnedWhenFindSubCategoryIsCalled() {
        instance.subCategories = Arrays.asList(subCategory);
        assertEquals("Incorrect category object", subCategory, instance.findSubCategory(CANDY).get());
    }
    private Category subCategory;

    /**
     * The findSubCategory() should return empty Optional.
     */
    @Test
    public void isEmptyOptionalReturnedWhenFindSubCategoryIsCalledButSubCategoryDoesntExist() {
        instance.subCategories = new LinkedList<>();
        assertFalse("findCategory should not find any subcategory", instance.findSubCategory(
                "nonexistent").isPresent());
    }

    /**
     * If subcategories list is empty, hasSubCategories() should return false. Otherwise it should
     * return true.
     */
    @Test
    public void testOfHasCategoriesMethod() {
        instance.subCategories = new LinkedList<>();
        assertFalse("Instance hasn't subcategories", instance.hasSubCategories());
        instance.subCategories.add(mock(Category.class));
        assertTrue("Instance has subcategories", instance.hasSubCategories());
    }

    /**
     * If transaction list is empty, hasTransactions() should return false. Otherwise it should
     * return true.
     */
    @Test
    public void testOfHasTransactionMethod() {
        instance.transactions = new LinkedList<>();
        assertFalse("Instance hasn't transaction", instance.hasTransactions());
        instance.transactions.add(mock(Transaction.class));
        assertTrue("Instance has transactions", instance.hasTransactions());
    }

    /**
     * The removeSubCategory() should remove subCategory from list of subcategories.
     */
    @Test
    public void isSubCategoryRemoveWhenRemoveSubCategoryIsCalled() {
        instance.subCategories = mock(List.class);
        instance.removeSubCategory(subCategory);
        verify(instance.subCategories).remove(subCategory);
    }

    /**
     * The addTransaction() should add a transaction to the list of transactions.
     */
    @Test
    public void isTransactionAddedWhenAddTransactionIsCalled() {
        final Transaction transaction = mock(Transaction.class);
        instance.transactions = mock(List.class);
        instance.addTransaction(transaction);
        verify(instance.transactions).add(transaction);
    }

    /**
     * The addTransaction should throw NullPointerException when transaction parameter is null.
     */
    @Test(expected = NullPointerException.class)
    public void isNullPointerExceptionThrownWhenAddTransactionWithNullParamIsCalled() {
        instance.transactions = mock(List.class);
        instance.addTransaction(null);
    }
    
    @Test
    public void testSettersAndGetters() {
        final String name = "test";
        instance.setName(name);
        assertThat(name, equalTo(instance.getName()));
    }

}
