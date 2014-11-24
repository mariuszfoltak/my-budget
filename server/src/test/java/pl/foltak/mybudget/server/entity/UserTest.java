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
 * Test for User entity.
 *
 * @author mariusz@foltak.pl
 */
public class UserTest {

    User instance;

    @Before
    public void setUp() {
        instance = new User();
    }

    /**
     * The addCategory method should add category to the list.
     */
    @Test
    public void isCategoryAddedWhenAddCategoryIsCalled() {
        instance.categories = mock(List.class);
        final Category category = mock(Category.class);
        instance.addCategory(category);
        verify(instance.categories).add(category);
    }

    /**
     * The addCategory() should throw NullPointerException when parameter is null.
     */
    @Test(expected = NullPointerException.class)
    public void isNullPointerExceptionThrownWhenAddCategoryWithNullParameterIsCalled() {
        instance.categories = mock(List.class);
        instance.addCategory(null);
    }

    /**
     * The findCategory method should return category.
     */
    @Test
    public void isCategoryReturnedWhenFindCategoryIsCalled() {
        final String food = "food";
        final Category category = mock(Category.class);
        when(category.getName()).thenReturn(food);

        instance.categories = Arrays.asList(category);

        assertSame("Another object of category", category, instance.findCategory(food).get());
    }

    /**
     * The findCategory method should return empty Optional if category with given name doesn't
     * exist.
     */
    @Test
    public void isNullReturnedWhenFindCategoryThatDoesntExist() {
        final String nonexistent = "nonexistent";
        instance.categories = new LinkedList<>();
        assertFalse("Not category should be present", instance.findCategory(nonexistent).isPresent());
    }

    /**
     * The removeCategory method should remove category from list.
     */
    @Test
    public void isCategoryRemovedWhenRemoveCategoryIsCalled() {
        final Category category = mock(Category.class);
        instance.categories = mock(List.class);

        instance.removeCategory(category);

        verify(instance.categories).remove(category);
    }

    /**
     * The getCategories method should return a copy of categories list.
     */
    @Test
    public void isReturnedCopyOfCategoryListWhenGetCategoriesIsCalled() {
        instance.categories = new LinkedList<>();
        assertNotSame("Returned list should be a copy", instance.categories,
                instance.getCategories());
    }

    /**
     * The addAccount method should add account to the accounts list.
     */
    @Test
    public void isAccountAddedWhenAddAccountIsCalled() {
        final Account account = mock(Account.class);
        instance.accounts = mock(List.class);
        instance.addAccount(account);
        verify(instance.accounts).add(account);
    }

    /**
     * The addAccount() should throw NullPointerException when parameter is null.
     */
    @Test(expected = NullPointerException.class)
    public void isNullPointerExceptionThrownWhenAddAccountWithNullParameterIsCalled() {
        instance.accounts = mock(List.class);
        instance.addAccount(null);
    }

    /**
     * The removeAccount method should remove account from accounts list.
     */
    @Test
    public void isAccountRemovedWhenRemoveAccountIsCalled() {
        final Account account = mock(Account.class);
        instance.accounts = mock(List.class);
        instance.removeAccount(account);
        verify(instance.accounts).remove(account);
    }

    /**
     * The findAccount method should return account with given name.
     */
    @Test
    public void isAccountReturnWhenFindAccountIsCalled() {
        final Account account = mock(Account.class);
        instance.accounts = Arrays.asList(account);
        when(account.getName()).thenReturn("wallet");
        assertSame(account, instance.findAccount("wallet").get());
    }

    /**
     * The findAccount method should return empty Optional, when account doesn't exist.
     */
    @Test
    public void isNullReturnedWhenFindAccountIsCalledAndTheAccountDoesntExist() {
        instance.accounts = new LinkedList<>();
        assertFalse("Optional should be empty", instance.findAccount("nonexistent").isPresent());
    }

    /**
     * The getAccounts method should return a copy of accounts list.
     */
    @Test
    public void isReturnedCopyOfAccountsListWhenGetAccountsIsCalled() {
        instance.accounts = new LinkedList<>();
        assertNotSame("Returned list should be a copy", instance.accounts,
                instance.getAccounts());
    }

    /**
     * The addTag method should add tag to the tags list.
     */
    @Test
    public void isTagAddedWhenAddTagIsCalled() {
        final Tag tag = mock(Tag.class);
        instance.tags = mock(List.class);
        instance.addTag(tag);
        verify(instance.tags).add(tag);
    }

    /**
     * The addTag() should throw NullPointerException when the parameter is null.
     */
    @Test(expected = NullPointerException.class)
    public void isNullPointerExceptionThrownWhenAddTagWithNullParameterIsCalled() {
        instance.tags = mock(List.class);
        instance.addTag(null);
    }

    /**
     * The findTag method should return tag with given name.
     */
    @Test
    public void isTagReturnWhenFindTagIsCalled() {
        final Tag tag = mock(Tag.class);
        instance.tags = Arrays.asList(tag);
        when(tag.getName()).thenReturn("wallet");
        assertSame(tag, instance.findTag("wallet").get());
    }

    /**
     * Method should return empty optional, when account doesn't exist.
     */
    @Test
    public void isNullReturnedWhenFindTagIsCalledAndTheTagDoesntExist() {
        instance.tags = new LinkedList<>();
        assertFalse("Optional should be empty", instance.findTag("nonexistent").isPresent());
    }

    /**
     * Method should return a copy of tags list.
     */
    @Test
    public void isReturnedCopyOfTagListWhenGetTagsIsCalled() {
        instance.tags = new LinkedList<>();
        assertNotSame("Returned list should be a copy", instance.tags,
                instance.getTags());
    }
}
