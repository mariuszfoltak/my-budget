package pl.foltak.mybudget.server.dao;

import pl.foltak.mybudget.server.rest.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.dao.exception.CategoryAlreadyExistsException;
import pl.foltak.mybudget.server.dao.exception.CategoryCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.User;
import static pl.foltak.mybudget.server.test.TestUtils.expectedException;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class MyBudgetDaoCategoryTest {

    private static final String FOOD = "food";
    private static final String CANDY = "candy";
    private static final String HOUSE = "house";
    private static final String USERNAME = "alibaba";
    private static final String NONEXISTENT = "nonexistent";

    private MyBudgetDao instance;
    private User user;
    private Category foodCategory;
    private Category candyCategory;
    private Category houseCategory;

    @Before
    public void setUp() {
        instance = spy(new MyBudgetDao());
        user = mock(User.class);
        foodCategory = mock(Category.class);
        candyCategory = mock(Category.class);
        houseCategory = mock(Category.class);

        doReturn(user).when(instance).getUserByName(USERNAME);
        when(user.findCategory(any())).thenReturn(Optional.ofNullable(null));
        when(user.findCategory(FOOD)).thenReturn(Optional.of(foodCategory));
        when(foodCategory.getName()).thenReturn(FOOD);
        when(foodCategory.findSubCategory(any())).thenReturn(Optional.ofNullable(null));
        when(foodCategory.findSubCategory(CANDY)).thenReturn(Optional.of(candyCategory));
        when(candyCategory.getName()).thenReturn(CANDY);
        when(houseCategory.getName()).thenReturn(HOUSE);
    }

    /**
     * Method addMainCategory should add given category to the user.
     *
     * @throws CategoryAlreadyExistsException
     */
    @Test
    public void isCategoryAddedToUserWhenAddingMainCategory() throws CategoryAlreadyExistsException {
        final String car = "car";
        final Category category = mock(Category.class);
        when(category.getName()).thenReturn(car);
        instance.addMainCategory(USERNAME, category);

        verify(user).addCategory(category);
    }

    /**
     * Method addMainCategory should throw exception, when given category already exists.
     *
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = CategoryAlreadyExistsException.class)
    public void isCategoryAlreadyExistsThrownWhenAddingExistingMainCategory()
            throws CategoryAlreadyExistsException {

        instance.addMainCategory(USERNAME, foodCategory);
    }

    /**
     * Method removeMainCategory should remove category from user.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test
    public void testRemovingCategoryFromUserWhenRemovingExistingMainCategory()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        instance.removeMainCategory(USERNAME, FOOD);
        verify(user).removeCategory(foodCategory);
    }

    /**
     * Method removeMainCategory should throw exception, when a category doesn't exist.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void testReturnNotFoundExceptionWhenRemovingNonexistingMainCategory()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        instance.removeMainCategory(USERNAME, NONEXISTENT);
    }

    /**
     * Method removeMainCategory should throw CategoryCantBeRemovedException if category has sub
     * categories. Method shouldn't remove any category.
     *
     * @throws CategoryNotFoundException
     */
    @Test
    public void isExceptionThrownWhenRemovingMainCategoryWithSubCategories()
            throws CategoryNotFoundException {

        when(foodCategory.hasSubCategories()).thenReturn(true);
        try {
            instance.removeMainCategory(USERNAME, FOOD);
            expectedException(BadRequestException.class);
        } catch (CategoryCantBeRemovedException ex) {
            verify(user, never()).removeCategory(any());
        }
    }

    /**
     * Method removeMainCategory should throw CategoryCantBeRemovedException if a category has
     * transactions. Method shouldn't remove any category.
     *
     * @throws CategoryNotFoundException
     */
    @Test
    public void isExceptionThrownWhenRemovingMainCategoryWithTransactions()
            throws CategoryNotFoundException {

        when(foodCategory.hasTransactions()).thenReturn(true);
        try {
            instance.removeMainCategory(USERNAME, FOOD);
            expectedException(BadRequestException.class);
        } catch (CategoryCantBeRemovedException ex) {
            verify(user, never()).removeCategory(any());
        }
    }

    /**
     * Method updateMainCategory should update category fields.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test
    public void isEntityUpdatedWhenModifyMainCategory()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        doNothing().when(instance).setCategoryFields(any(), any());
        instance.updateMainCategory(USERNAME, FOOD, foodCategory);
        verify(instance).setCategoryFields(foodCategory, foodCategory);
    }

    /**
     * Method updateMainCategory should throw CategoryNotFoundException if a category with given
     * name doesn't exist.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isNotFoundThrownWhenUpdatingNonexistedMainCategory()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        instance.updateMainCategory(USERNAME, NONEXISTENT, houseCategory);
    }

    /**
     * Method updateMainCategory should throw CategoryAlreadyExistsException if we a category with
     * new name already exists.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = CategoryAlreadyExistsException.class)
    public void isConflictExceptionThrownWhenNewCategoryNameAlreadyExists()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        Category categoryValues = mock(Category.class);
        when(categoryValues.getName()).thenReturn(FOOD);
        when(user.findCategory(HOUSE)).thenReturn(Optional.of(houseCategory));
        doNothing().when(instance).setCategoryFields(any(), any());

        instance.updateMainCategory(USERNAME, HOUSE, categoryValues);
    }

    /**
     * Method addSubCategory should add a sub category to parent category.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test
    public void isSubCategoryAddedToParentCategory()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        instance.addSubCategory(USERNAME, FOOD, houseCategory);
        verify(foodCategory).addSubCategory(houseCategory);
    }

    /**
     * Method addSubCategory should throw CategoryNotFoundException if main category doesn't exists.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isCategoryNotFoundExceptionThrownWhenCreatingSubcategoryAndMainCategoryDoesntExist()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        instance.addSubCategory(USERNAME, NONEXISTENT, houseCategory);
    }

    /**
     * Method should throw CategoryAlreadyExistsException when sub category already exists.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = CategoryAlreadyExistsException.class)
    public void isCategoryAlreadyExistsExceptionThrownWhenAddingSubCategoryAlreadyExist()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        instance.addSubCategory(USERNAME, FOOD, candyCategory);
    }

    /**
     * Method removeSubCategory should remove a sub category from main category.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test
    public void isSubCategoryRemoved()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        instance.removeSubCategory(USERNAME, FOOD, CANDY);
        verify(foodCategory).removeSubCategory(candyCategory);
    }

    /**
     * Method removeSubCategory should throw CategoryNotFoundException if main category doesn't
     * exist.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isCategoryNotFoundExceptionThrownWhenRemovingSubCategoryButMainCategoryDoesntExist()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        instance.removeSubCategory(USERNAME, NONEXISTENT, CANDY);
    }

    /**
     * Method removeSubCategory should throw CategoryNotFoundException if sub category doesn't
     * exist.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isCategoryNotFoundExceptionThrownWhenRemovingSubcategoryThatDoesntExist()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        instance.removeSubCategory(USERNAME, FOOD, NONEXISTENT);
    }

    /**
     * Method removeSubCategory should throw CategoryCantBeRemovedException when sub category has a
     * transactions.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test(expected = CategoryCantBeRemovedException.class)
    public void isCategoryCantBeRemovedExceptionThrownWhenRemovingSubCategoryThatHasTransactions()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        when(candyCategory.hasTransactions()).thenReturn(Boolean.TRUE);
        instance.removeSubCategory(USERNAME, FOOD, CANDY);
    }

    /**
     * Method updateSubCategory should update entity.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test
    public void isSubCategoryUpdated()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        doNothing().when(instance).setCategoryFields(any(), any());
        instance.updateSubCategory(USERNAME, FOOD, CANDY, candyCategory);
        verify(instance).setCategoryFields(candyCategory, candyCategory);
    }

    /**
     * Method updateSubCategory should throw CategoryNotFoundException when main category doesn't
     * exist.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isCategoryNotFoundExceptionThrownWhenMainCategoryDoesntExist()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        instance.updateSubCategory(USERNAME, NONEXISTENT, CANDY, houseCategory);
    }

    /**
     * Method updateSubCategory should throw CategoryNotFoundException when sub category doesn't
     * exist.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isCategoryNotFoundExceptionThrownWhenSubCategoryDoesntExist()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        instance.updateSubCategory(USERNAME, FOOD, NONEXISTENT, houseCategory);
    }

    /**
     * Method updateSubCategory should throw CategoryAlreadyExistsException if sub category with new
     * name already exists.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = CategoryAlreadyExistsException.class)
    public void isCategoryAlreadyExistsExceptionThrownWhenSubCategoryWithNewNameAlreadyExist()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        when(foodCategory.findSubCategory("fruits")).thenReturn(Optional.of(mock(Category.class)));
        instance.updateSubCategory(USERNAME, FOOD, "fruits", candyCategory);
    }

    /**
     * Method should return categories list of user.
     */
    @Test
    public void testReturnCategoriesListWhenGettingAllCategories() {
        List<Category> categories = mock(List.class);
        when(user.getCategories()).thenReturn(categories);
        List<Category> result = (List<Category>) instance.getAllCategories(USERNAME);
        assertEquals("List of categories it's not equals", categories, result);
    }

    /**
     * Method should return sub categories list of main category.
     *
     * @throws CategoryNotFoundException
     */
    @Test
    public void isSubCategoriesListReturnWhenGettingSubCategories() throws CategoryNotFoundException {
        List<Category> categories = mock(List.class);
        when(foodCategory.getSubCategories()).thenReturn(categories);
        List<Category> result = (List<Category>) instance.getSubCategories(USERNAME, FOOD);
        assertEquals("List of categories it's not equals", categories, result);
    }

    /**
     * Method getSubCategories should throw CategoryNotFoundException if main category doesn't
     * exist.
     *
     * @throws CategoryNotFoundException
     */
    @Test(expected = CategoryNotFoundException.class)
    public void isCategoryNotFoundExceptionThrownWhenGettingSubCategoriesFromCategoryThatDoesntExist()
            throws CategoryNotFoundException {

        instance.getSubCategories(USERNAME, NONEXISTENT);
    }
}
