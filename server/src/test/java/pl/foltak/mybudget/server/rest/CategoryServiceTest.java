package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
            expectedException(ConflictException.class);
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
            expectedException(BadRequestException.class);
        } catch (BadRequestException e) {
            verify(user, never()).removeCategory(any());
        }
    }

    @Test
    public void testReturnBadRequestWhenRemovingMainCategoryWithTransactions() {
        when(mainCategory.hasTransactions()).thenReturn(true);
        try {
            instance.removeParentCategory(FOOD);
            expectedException(BadRequestException.class);
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
    public void testReturnLocationHeaderWhenAddingSubcategory() {
        String fruits = "fruits";
        Response response = instance.addCategory(FOOD, new Category(fruits));
        assertEquals("Location header isn't equal to new category", createURI(FOOD, fruits), response.getLocation());
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundWhenCreatingSubcategoryAndMainCategoryDoesntExist() {
        instance.addCategory("nonExisting", new Category("test"));
    }

    @Test
    public void testThrowConflictWhenCreatingSubcategoryAlreadyExist() {
        try {
            instance.addCategory(FOOD, new Category(CANDY));
            expectedException(ConflictException.class);
        } catch (Exception e) {
            verify(mainCategory, never()).addCategory(any());
        }
    }

    @Test
    public void testUpdateEntityWhenRemovingSubcategory() {
        instance.removeSubcategory(FOOD, CANDY);
        verify(mainCategory).removeSubcategory(CANDY);
    }

    @Test
    public void testReturnOkStatusWhenRemovingSubcategory() {
        Response response = instance.removeSubcategory(FOOD, CANDY);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundWhenRemovingSubcategoryAndParentCategoryDoesntExist() {
        instance.removeSubcategory("nonExist", CANDY);
    }

    @Test
    public void testThrowNotFoundExceptionWhenRemovingSubcategoryThatDoesntExist() {
        try {
            instance.removeSubcategory(FOOD, "nonExist");
            expectedException(NotFoundException.class);
        } catch (NotFoundException e) {
            verify(mainCategory, never()).removeSubcategory(any());
        }

    }

    @Test
    public void testThrowBadRequestExceptionWhenRemovingSubcategoryThatHasTransactions() {
        when(subCategory.hasTransactions()).thenReturn(Boolean.TRUE);
        try {
            instance.removeSubcategory(FOOD, CANDY);
            expectedException(BadRequestException.class);
        } catch (Exception e) {
            verify(mainCategory, never()).removeSubcategory(any());
        }
    }

    @Test
    public void testReturnOkStatusWhenModifingSubcategory() {
        Response response = instance.editSubcategory(FOOD, CANDY, new Category("newCandy"));
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test
    public void testUpdateEntityWhenModifingSubcategory() {
        instance.editSubcategory(FOOD, CANDY, new Category("newCandy"));
        verify(subCategory, times(1)).setName("newCandy");
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundExceptionWhenMainCategoryDoesntExist() {
        instance.editSubcategory("nonExist", CANDY, new Category("newCandy"));
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundExceptionWhenSubCategoryDoesntExist() {
        instance.editSubcategory(FOOD, "nonExist", new Category("newCandy"));
    }

    @Test
    public void testThrowConflictExceptionWhenSubCategoryWithNewNameAlreadyExist() {
        when(subCategory.hasTransactions()).thenReturn(Boolean.TRUE);
        try {
            instance.editSubcategory(FOOD, CANDY, subCategory);
            expectedException(ConflictException.class);
        } catch (ConflictException e) {
            verify(subCategory, never()).setName(any());
        }
    }

    @Test
    public void testReturnOkStatusWhenGettingAllCategories() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        instance.getAllCategories(response);
        verify(response).setStatus(200);
    }

    @Test
    public void testReturnCategoriesListWhenGettingAllCategories() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        List<Category> categories = prepareListOfCategories();
        when(user.getCategories()).thenReturn(categories);
        List<Category> result = instance.getAllCategories(response);
        assertEquals("List of categories it's not equals", categories, result);
    }

    private List<Category> prepareListOfCategories() {
        List<Category> categories = new LinkedList<>();
        Category mainFirst = new Category("eating");
        mainFirst.setCategories(new LinkedList<>());
        mainFirst.getCategories().add(new Category("fruits"));
        mainFirst.getCategories().add(new Category("candy"));
        Category mainSecond = new Category("driving");
        mainSecond.setCategories(new LinkedList<>());
        mainSecond.getCategories().add(new Category("fuel"));
        mainSecond.getCategories().add(new Category("parts"));
        categories.add(mainFirst);
        categories.add(mainSecond);
        return categories;
    }

//    TODO: get subcategories return 200 OK
//    TODO: get subcategories from category
//    TODO: throw not found exception when category doesnt exist

    @Test
    public void testUpdateEntityWhenRemovingSubcategory() {
        instance.removeSubcategory(FOOD, CANDY);
        verify(mainCategory).removeSubcategory(CANDY);
    }

    @Test
    public void testReturnOkStatusWhenRemovingSubcategory() {
        Response response = instance.removeSubcategory(FOOD, CANDY);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundWhenRemovingSubcategoryAndParentCategoryDoesntExist() {
        instance.removeSubcategory("nonExist", CANDY);
    }

    @Test
    public void testThrowNotFoundExceptionWhenRemovingSubcategoryThatDoesntExist() {
        try {
            instance.removeSubcategory(FOOD, "nonExist");
            expectedException(NotFoundException.class);
        } catch (NotFoundException e) {
            verify(mainCategory, never()).removeSubcategory(any());
        }

    }

    @Test
    public void testThrowBadRequestExceptionWhenRemovingSubcategoryThatHasTransactions() {
        when(subCategory.hasTransactions()).thenReturn(Boolean.TRUE);
        try {
            instance.removeSubcategory(FOOD, CANDY);
            expectedException(BadRequestException.class);
        } catch (Exception e) {
            verify(mainCategory, never()).removeSubcategory(any());
        }
    }

    @Test
    public void testReturnOkStatusWhenModifingSubcategory() {
        Response response = instance.editSubcategory(FOOD, CANDY, new Category("newCandy"));
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test
    public void testUpdateEntityWhenModifingSubcategory() {
        instance.editSubcategory(FOOD, CANDY, new Category("newCandy"));
        verify(subCategory, times(1)).setName("newCandy");
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundExceptionWhenMainCategoryDoesntExist() {
        instance.editSubcategory("nonExist", CANDY, new Category("newCandy"));
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundExceptionWhenSubCategoryDoesntExist() {
        instance.editSubcategory(FOOD, "nonExist", new Category("newCandy"));
    }

    @Test
    public void testThrowConflictExceptionWhenSubCategoryWithNewNameAlreadyExist() {
        when(subCategory.hasTransactions()).thenReturn(Boolean.TRUE);
        try {
            instance.editSubcategory(FOOD, CANDY, subCategory);
            expectedException(ConflictException.class);
        } catch (ConflictException e) {
            verify(subCategory, never()).setName(any());
        }
    }

    @Test
    public void testReturnOkStatusWhenGettingAllCategories() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        instance.getAllCategories(response);
        verify(response).setStatus(200);
    }

    @Test
    public void testReturnCategoriesListWhenGettingAllCategories() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        List<Category> categories = prepareListOfCategories();
        when(user.getCategories()).thenReturn(categories);
        List<Category> result = instance.getAllCategories(response);
        assertEquals("List of categories it's not equals", categories, result);
    }

    private List<Category> prepareListOfCategories() {
        List<Category> categories = new LinkedList<>();
        Category mainFirst = new Category("eating");
        mainFirst.setCategories(new LinkedList<>());
        mainFirst.getCategories().add(new Category("fruits"));
        mainFirst.getCategories().add(new Category("candy"));
        Category mainSecond = new Category("driving");
        mainSecond.setCategories(new LinkedList<>());
        mainSecond.getCategories().add(new Category("fuel"));
        mainSecond.getCategories().add(new Category("parts"));
        categories.add(mainFirst);
        categories.add(mainSecond);
        return categories;
    }

//    TODO: get subcategories return 200 OK
//    TODO: get subcategories from category
//    TODO: throw not found exception when category doesnt exist
    
    private static URI createURI(String firstLevelCategory, String secondLevelCategory) {
        return URI.create("category/" + firstLevelCategory + "/" + secondLevelCategory);
    }

    private static URI createURI(String firstLevelCategory) {
        return URI.create("category/" + firstLevelCategory);
    }

    private <T extends Exception> void expectedException(Class<T> exceptionClass) {
        fail("Expected exception: " + exceptionClass.getName());
    }
}
