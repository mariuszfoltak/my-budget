package pl.foltak.mybudget.server.rest;

import java.net.URI;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.User;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class CategoryServiceTest {

    private static final String FOOD = "food";
    private static final String CANDY = "candy";

    private CategoryService instance;
    private User user;
    private Category mainCategory;
    private Category subCategory;

    @Before
    public void setUp() {
        instance = new CategoryService();
        instance.user = user = mock(User.class);
        mainCategory = mock(Category.class);
        subCategory = mock(Category.class);

        when(user.findCategory(FOOD)).thenReturn(mainCategory);
        when(mainCategory.getName()).thenReturn(FOOD);
        when(mainCategory.findCategory(CANDY)).thenReturn(subCategory);
    }

    @Test
    public void testAddingMainCategoryStatusCodeIsCreated() {
        Response response = instance.addParentCategory(new Category("car"));
        assertEquals("Status code isn't equal to 201,", 201, response.getStatus());
    }

    @Test
    public void testAddingMainCategoryReturnLocationHeader() {
        String car = "car";
        Response response = instance.addParentCategory(new Category(car));
        assertEquals("Location header isn't equal to new category,", createURI(car), response.getLocation());
    }

    @Test
    public void testAddingMainCategoryAddCategoryToUser() {
        final String car = "car";
        instance.addParentCategory(new Category(car));

        verify(user).addCategory(new Category(car));
    }

    @Test
    public void testAddingExistingMainCategory() {
        try {
            instance.addParentCategory(mainCategory);
            fail("Expected exception: " + ConflictException.class.getName());
        } catch (ConflictException e) {
            verify(user, never()).addCategory(any(Category.class));
        }
    }

    @Test
    public void testReturnOkStatusCodeWhenRemovingExistingMainCategory() {
        Response response = instance.removeParentCategory(FOOD);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test
    public void testRemovingCategoryFromUserWhenRemovingExistingMainCategory() {
        instance.removeParentCategory(FOOD);
        verify(user, times(1)).removeCategory(FOOD);
    }

    @Test(expected = NotFoundException.class)
    public void testReturnNotFoundExceptionWhenRemovingNonexistingMainCategory() {
        instance.removeParentCategory("non-existing");
    }

    @Test
    public void testReturnBadRequestWhenRemovingMainCategoryWithSubcategories() {
        when(mainCategory.hasSubCategories()).thenReturn(true);
        try {
            instance.removeParentCategory(FOOD);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            verify(user, never()).removeCategory(any());
        }
    }

    @Test
    public void testReturnBadRequestWhenRemovingMainCategoryWithTransactions() {
        when(mainCategory.hasTransactions()).thenReturn(true);
        try {
            instance.removeParentCategory(FOOD);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            verify(user, never()).removeCategory(any());
        }
    }

    @Test
    public void testUpdatingEntityWhenModifyMainCategory() {
        final String newFood = "newFood";
        instance.editMainCategory(FOOD, new Category(newFood));
        verify(mainCategory, times(1)).setName(newFood);
    }

    @Test
    public void testReturnOkStatusWhenModifyMainCategory() {
        Response response = instance.editMainCategory(FOOD, new Category("newFood"));
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test(expected = NotFoundException.class)
    public void testReturnNotFoundWhenModifingNonexistingMainCategory() {
        instance.editMainCategory("nonExisting", new Category("newCategory"));
    }

    @Test
    public void testAddSecondLevelCategoryAddCategoryToParentCategory() {
        final String categoryName = "fruits";

        instance.addCategory(FOOD, new Category(categoryName));
        verify(mainCategory).addCategory(new Category(categoryName));
    }

    @Test
    public void testAddSecondLevelCategoryReturnCreatedStatusCode() {
        Response response = instance.addCategory(FOOD, new Category("fruits"));
        assertEquals("Status code isn't equal to 201", 201, response.getStatus());
    }

    @Test
    public void testAddSecondLevelCategoryReturnLocationHeader() {
        String fruits = "fruits";
        Response response = instance.addCategory(FOOD, new Category(fruits));
        assertEquals("Location header isn't equal to new category", createURI(FOOD, fruits), response.getLocation());
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundWhenMainCategoryDoesntExist() {
        instance.addCategory("nonExisting", new Category("test"));
    }

    @Test
    public void testThrowConflictWhenCreatedCategoryAlreadyExist() {
        try {
            instance.addCategory(FOOD, new Category(CANDY));
            fail("Expected exception: " + ConflictException.class.getName());
        } catch (Exception e) {
            verify(mainCategory, never()).addCategory(any());
        }
    }
//    TODO: When we try to modify subcategory and parent category doesn't exist, we should get 404
//    TODO: When we try to modify non-exist subcategory, we should get 404
//    TODO: When we try to modify subcategory to already exist, we should get 409
//    TODO: When we try to delete subcategory and parent category doesn't exist, we should get 404
//    TODO: When we try to delete non-exist subcategory, we should get 404
//    TODO: When we try to delete subcategory, then we should move all transactions to another category
//    TODO: get all categories

    private static URI createURI(String firstLevelCategory, String secondLevelCategory) {
        return URI.create("category/" + firstLevelCategory + "/" + secondLevelCategory);
    }

    private static URI createURI(String firstLevelCategory) {
        return URI.create("category/" + firstLevelCategory);
    }
}
