package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.User;
import static pl.foltak.mybudget.server.test.TestUtils.expectedException;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class CategoryServiceTest {

    private static final String FOOD = "food";
    private static final String CANDY = "candy";
    private static final String HOUSE = "house";

    private CategoryService instance;
    private User user;
    private Category mainCategory;
    private Category subCategory;
    private Category houseCategory;

    @Before
    public void setUp() {
        instance = spy(new CategoryService());
        user = mock(User.class);
        mainCategory = mock(Category.class);
        subCategory = mock(Category.class);
        houseCategory = mock(Category.class);

        doReturn(user).when(instance).getUser();
        when(user.findCategory(any())).thenReturn(Optional.ofNullable(null));
        when(user.findCategory(FOOD)).thenReturn(Optional.of(mainCategory));
        when(mainCategory.getName()).thenReturn(FOOD);
        when(mainCategory.findSubCategory(any())).thenReturn(Optional.ofNullable(null));
        when(mainCategory.findSubCategory(CANDY)).thenReturn(Optional.of(subCategory));
        when(subCategory.getName()).thenReturn(CANDY);
        when(houseCategory.getName()).thenReturn(HOUSE);
    }

    @Test
    public void testAddingMainCategoryStatusCodeIsCreated() {
        Response response = instance.addMainCategory(mock(Category.class));
        assertEquals("Status code isn't equal to 201,", 201, response.getStatus());
    }

    @Test
    public void testAddingMainCategoryReturnLocationHeader() {
        String car = "car";
        final Category category = mock(Category.class);
        when(category.getName()).thenReturn(car);
        Response response = instance.addMainCategory(category);
        assertEquals("Location header isn't equal to new category,", createURI(car),
                response.getLocation());
    }

    @Test
    public void testAddingMainCategoryAddCategoryToUser() {
        final String car = "car";
        final Category category = mock(Category.class);
        when(category.getName()).thenReturn(car);
        instance.addMainCategory(category);

        verify(user).addCategory(category);
    }

    @Test
    public void testAddingExistingMainCategory() {
        try {
            instance.addMainCategory(mainCategory);
            expectedException(ConflictException.class);
        } catch (ConflictException e) {
            verify(user, never()).addCategory(any(Category.class));
        }
    }

    @Test
    public void testReturnOkStatusCodeWhenRemovingExistingMainCategory() {
        Response response = instance.removeMainCategory(FOOD);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test
    public void testRemovingCategoryFromUserWhenRemovingExistingMainCategory() {
        instance.removeMainCategory(FOOD);
        verify(user, times(1)).removeCategory(mainCategory);
    }

    @Test(expected = NotFoundException.class)
    public void testReturnNotFoundExceptionWhenRemovingNonexistingMainCategory() {
        instance.removeMainCategory("nonexistent");
    }

    @Test
    public void testReturnBadRequestWhenRemovingMainCategoryWithSubcategories() {
        when(mainCategory.hasSubCategories()).thenReturn(true);
        try {
            instance.removeMainCategory(FOOD);
            expectedException(BadRequestException.class);
        } catch (BadRequestException e) {
            verify(user, never()).removeCategory(any());
        }
    }

    @Test
    public void testReturnBadRequestWhenRemovingMainCategoryWithTransactions() {
        when(mainCategory.hasTransactions()).thenReturn(true);
        try {
            instance.removeMainCategory(FOOD);
            expectedException(BadRequestException.class);
        } catch (BadRequestException e) {
            verify(user, never()).removeCategory(any());
        }
    }

    @Test
    public void testUpdatingEntityWhenModifyMainCategory() {
        instance.editMainCategory(FOOD, houseCategory);
        verify(mainCategory, times(1)).setName(HOUSE);
    }

    @Test
    public void testReturnOkStatusWhenModifyMainCategory() {
        Response response = instance.editMainCategory(FOOD, houseCategory);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test(expected = NotFoundException.class)
    public void testReturnNotFoundWhenModifingNonexistingMainCategory() {
        instance.editMainCategory("nonexistent", houseCategory);
    }

    @Test
    public void testAddSecondLevelCategoryAddCategoryToParentCategory() {
        instance.addSubCategory(FOOD, houseCategory);
        verify(mainCategory).addSubCategory(houseCategory);
    }

    @Test
    public void testAddSecondLevelCategoryReturnCreatedStatusCode() {
        Response response = instance.addSubCategory(FOOD, houseCategory);
        assertEquals("Status code isn't equal to 201", 201, response.getStatus());
    }

    @Test
    public void testReturnLocationHeaderWhenAddingSubcategory() {
        Response response = instance.addSubCategory(FOOD, houseCategory);
        assertEquals("Location header isn't equal to new category", createURI(FOOD, HOUSE),
                response.getLocation());
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundWhenCreatingSubcategoryAndMainCategoryDoesntExist() {
        instance.addSubCategory("nonexistent", houseCategory);
    }

    @Test
    public void testThrowConflictWhenCreatingSubcategoryAlreadyExist() {
        try {
            instance.addSubCategory(FOOD, subCategory);
            expectedException(ConflictException.class);
        } catch (Exception e) {
            verify(mainCategory, never()).addSubCategory(any());
        }
    }

    @Test
    public void testUpdateEntityWhenRemovingSubcategory() {
        instance.removeSubCategory(FOOD, CANDY);
        verify(mainCategory).removeSubCategory(subCategory);
    }

    @Test
    public void testReturnOkStatusWhenRemovingSubcategory() {
        Response response = instance.removeSubCategory(FOOD, CANDY);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundWhenRemovingSubcategoryAndParentCategoryDoesntExist() {
        instance.removeSubCategory("nonexistent", CANDY);
    }

    @Test
    public void testThrowNotFoundExceptionWhenRemovingSubcategoryThatDoesntExist() {
        try {
            instance.removeSubCategory(FOOD, "nonexistent");
            expectedException(NotFoundException.class);
        } catch (NotFoundException e) {
            verify(mainCategory, never()).removeSubCategory(any());
        }

    }

    @Test
    public void testThrowBadRequestExceptionWhenRemovingSubcategoryThatHasTransactions() {
        when(subCategory.hasTransactions()).thenReturn(Boolean.TRUE);
        try {
            instance.removeSubCategory(FOOD, CANDY);
            expectedException(BadRequestException.class);
        } catch (Exception e) {
            verify(mainCategory, never()).removeSubCategory(any());
        }
    }

    @Test
    public void testReturnOkStatusWhenModifingSubcategory() {
        Response response = instance.editSubCategory(FOOD, CANDY, houseCategory);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    @Test
    public void testUpdateEntityWhenModifingSubcategory() {
        instance.editSubCategory(FOOD, CANDY, houseCategory);
        verify(subCategory, times(1)).setName(HOUSE);
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundExceptionWhenMainCategoryDoesntExist() {
        instance.editSubCategory("nonexistent", CANDY, houseCategory);
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundExceptionWhenSubCategoryDoesntExist() {
        instance.editSubCategory(FOOD, "nonexistent", houseCategory);
    }

    @Test
    public void testThrowConflictExceptionWhenSubCategoryWithNewNameAlreadyExist() {
        when(mainCategory.findSubCategory(CANDY)).thenReturn(Optional.of(subCategory));
        when(subCategory.getName()).thenReturn(CANDY);
        try {
            instance.editSubCategory(FOOD, CANDY, subCategory);
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
        List<Category> categories = mock(List.class);
        when(user.getCategories()).thenReturn(categories);
        List<Category> result = instance.getAllCategories(mock(HttpServletResponse.class));
        assertEquals("List of categories it's not equals", categories, result);
    }

    @Test
    public void testReturnOkStatusWhenGettingSubcategories() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        instance.getSubcategories(FOOD, response);
        verify(response).setStatus(200);
    }

    @Test
    public void testReturnCategoriesListWhenGettingSubcategories() {
        List<Category> categories = mock(List.class);
        when(mainCategory.getSubCategories()).thenReturn(categories);
        List<Category> result = instance.getSubcategories(FOOD, mock(HttpServletResponse.class));
        assertEquals("List of categories it's not equals", categories, result);
    }

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundExceptionWhenGettingSubcategoriesFromCategoryThatDoesntExist() {
        instance.getSubcategories("nonexistent", mock(HttpServletResponse.class));
    }

    private static URI createURI(String firstLevelCategory, String secondLevelCategory) {
        return URI.create("category/" + firstLevelCategory + "/" + secondLevelCategory);
    }

    private static URI createURI(String firstLevelCategory) {
        return URI.create("category/" + firstLevelCategory);
    }

}
