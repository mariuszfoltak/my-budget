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
    private Category subCategory;
    private Category houseCategory;

    @Before
    public void setUp() {
        instance = spy(new MyBudgetDao());
        user = mock(User.class);
        foodCategory = mock(Category.class);
        subCategory = mock(Category.class);
        houseCategory = mock(Category.class);

        doReturn(user).when(instance).getUserByName(USERNAME);
        when(user.findCategory(any())).thenReturn(Optional.ofNullable(null));
        when(user.findCategory(FOOD)).thenReturn(Optional.of(foodCategory));
        when(foodCategory.getName()).thenReturn(FOOD);
        when(foodCategory.findSubCategory(any())).thenReturn(Optional.ofNullable(null));
        when(foodCategory.findSubCategory(CANDY)).thenReturn(Optional.of(subCategory));
        when(subCategory.getName()).thenReturn(CANDY);
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

//
//    @Test
//    public void testAddSecondLevelCategoryAddCategoryToParentCategory() {
//        instance.addSubCategory(FOOD, houseCategory);
//        verify(mainCategory).addSubCategory(houseCategory);
//    }
//
//    @Test
//    public void testAddSecondLevelCategoryReturnCreatedStatusCode() {
//        Response response = instance.addSubCategory(FOOD, houseCategory);
//        assertEquals("Status code isn't equal to 201", 201, response.getStatus());
//    }
//
//    @Test
//    public void testReturnLocationHeaderWhenAddingSubcategory() {
//        Response response = instance.addSubCategory(FOOD, houseCategory);
//        assertEquals("Location header isn't equal to new category", createURI(FOOD, HOUSE),
//                response.getLocation());
//    }
//
//    @Test(expected = NotFoundException.class)
//    public void testThrowNotFoundWhenCreatingSubcategoryAndMainCategoryDoesntExist() {
//        instance.addSubCategory("nonexistent", houseCategory);
//    }
//
//    @Test
//    public void testThrowConflictWhenCreatingSubcategoryAlreadyExist() {
//        try {
//            instance.addSubCategory(FOOD, subCategory);
//            expectedException(ConflictException.class);
//        } catch (Exception e) {
//            verify(mainCategory, never()).addSubCategory(any());
//        }
//    }
//
//    @Test
//    public void testUpdateEntityWhenRemovingSubcategory() {
//        instance.removeSubCategory(FOOD, CANDY);
//        verify(mainCategory).removeSubCategory(subCategory);
//    }
//
//    @Test
//    public void testReturnOkStatusWhenRemovingSubcategory() {
//        Response response = instance.removeSubCategory(FOOD, CANDY);
//        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
//    }
//
//    @Test(expected = NotFoundException.class)
//    public void testThrowNotFoundWhenRemovingSubcategoryAndParentCategoryDoesntExist() {
//        instance.removeSubCategory("nonexistent", CANDY);
//    }
//
//    @Test
//    public void testThrowNotFoundExceptionWhenRemovingSubcategoryThatDoesntExist() {
//        try {
//            instance.removeSubCategory(FOOD, "nonexistent");
//            expectedException(NotFoundException.class);
//        } catch (NotFoundException e) {
//            verify(mainCategory, never()).removeSubCategory(any());
//        }
//
//    }
//
//    @Test
//    public void testThrowBadRequestExceptionWhenRemovingSubcategoryThatHasTransactions() {
//        when(subCategory.hasTransactions()).thenReturn(Boolean.TRUE);
//        try {
//            instance.removeSubCategory(FOOD, CANDY);
//            expectedException(BadRequestException.class);
//        } catch (Exception e) {
//            verify(mainCategory, never()).removeSubCategory(any());
//        }
//    }
//
//    @Test
//    public void testReturnOkStatusWhenModifingSubcategory() {
//        Response response = instance.editSubCategory(FOOD, CANDY, houseCategory);
//        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
//    }
//
//    @Test
//    public void testUpdateEntityWhenModifingSubcategory() {
//        instance.editSubCategory(FOOD, CANDY, houseCategory);
//        verify(subCategory, times(1)).setName(HOUSE);
//    }
//
//    @Test(expected = NotFoundException.class)
//    public void testThrowNotFoundExceptionWhenMainCategoryDoesntExist() {
//        instance.editSubCategory("nonexistent", CANDY, houseCategory);
//    }
//
//    @Test(expected = NotFoundException.class)
//    public void testThrowNotFoundExceptionWhenSubCategoryDoesntExist() {
//        instance.editSubCategory(FOOD, "nonexistent", houseCategory);
//    }
//
//    @Test
//    public void testThrowConflictExceptionWhenSubCategoryWithNewNameAlreadyExist() {
//        when(mainCategory.findSubCategory(CANDY)).thenReturn(Optional.of(subCategory));
//        when(subCategory.getName()).thenReturn(CANDY);
//        try {
//            instance.editSubCategory(FOOD, CANDY, subCategory);
//            expectedException(ConflictException.class);
//        } catch (ConflictException e) {
//            verify(subCategory, never()).setName(any());
//        }
//    }
//
//    @Test
//    public void testReturnOkStatusWhenGettingAllCategories() {
//        int statusCode = instance.getAllCategories().getStatus();
//        assertEquals("Incorrect status code", 200, statusCode);
//    }
//
//    @Test
//    public void testReturnCategoriesListWhenGettingAllCategories() {
//        List<Category> categories = mock(List.class);
//        when(user.getCategories()).thenReturn(categories);
//        List<Category> result = (List<Category>) instance.getAllCategories().getEntity();
//        assertEquals("List of categories it's not equals", categories, result);
//    }
//
//    @Test
//    public void testReturnOkStatusWhenGettingSubcategories() {
//        final int statusCode = instance.getSubcategories(FOOD).getStatus();
//        assertEquals("Incorrect status code", 200, statusCode);
//    }
//
//    @Test
//    public void testReturnCategoriesListWhenGettingSubcategories() {
//        List<Category> categories = mock(List.class);
//        when(mainCategory.getSubCategories()).thenReturn(categories);
//        List<Category> result = (List<Category>) instance.getSubcategories(FOOD).getEntity();
//        assertEquals("List of categories it's not equals", categories, result);
//    }
//
//    @Test(expected = NotFoundException.class)
//    public void testThrowNotFoundExceptionWhenGettingSubcategoriesFromCategoryThatDoesntExist() {
//        instance.getSubcategories("nonexistent");
//    }
}
