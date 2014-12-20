package pl.foltak.mybudget.server.rest;

import pl.foltak.mybudget.server.dao.exception.CategoryAlreadyExistsException;
import java.net.URI;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.dao.MyBudgetDaoLocal;
import pl.foltak.mybudget.server.dao.exception.CategoryCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class CategoryServiceTest {

    private static final String FOOD = "food";
    private static final String CANDY = "candy";
    private static final String HOUSE = "house";
    private static final String USERNAME = "alibaba";
    private static final String NONEXISTENT = "nonexistent";

    private CategoryService instance;
    private Category mainCategory;
    private Category subCategory;
    private Category houseCategory;
    private MyBudgetDaoLocal dao;

    @Before
    public void setUp() {
        instance = spy(new CategoryService());
        dao = mock(MyBudgetDaoLocal.class);

        mainCategory = mock(Category.class);
        subCategory = mock(Category.class);
        houseCategory = mock(Category.class);

        doReturn(USERNAME).when(instance).getUsername();
        doReturn(dao).when(instance).getDao();
        
        when(houseCategory.getName()).thenReturn(HOUSE);
    }

    /**
     * Method addMainCategory should return a 201 Created status after adds a category.
     */
    @Test
    public void isCreatedStatusReturnedWhenAddingMainCategory() {
        Response response = instance.addMainCategory(mock(Category.class));
        assertEquals("Status code isn't equal to 201,", 201, response.getStatus());
    }

    /**
     * Method addMainCategory should return a location header after adds a category.
     */
    @Test
    public void isLocationHeaderReturnedWhenAddingMainCategory() {
        Response response = instance.addMainCategory(houseCategory);
        assertEquals("Location header isn't equal to new category,", createURI(HOUSE),
                response.getLocation());
    }

    /**
     * Method addMainCategory should call MyBudgetDAO
     *
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryAlreadyExistsException
     */
    @Test
    public void isMyBudgetDaoCalledWhenAddingMainCategory()
            throws CategoryAlreadyExistsException {
        instance.addMainCategory(houseCategory);
        verify(dao).addCategory(USERNAME, houseCategory);
    }

    /**
     * Method addMainCategory should throw ConflictException when category with given name already
     * exists.
     *
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryAlreadyExistsException
     */
    @Test(expected = ConflictException.class)
    public void isConflictExceptionThrownWhenAddingExistingMainCategory()
            throws CategoryAlreadyExistsException {
        doThrow(CategoryAlreadyExistsException.class).when(dao)
                .addCategory(USERNAME, mainCategory);
        instance.addMainCategory(mainCategory);
    }

    /**
     * Method removeMainCategory should return 200 OK status after removed category.
     */
    @Test
    public void isOkStatusReturnedWhenRemovingExistingMainCategory() {

        Response response = instance.removeMainCategory(FOOD);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    /**
     * Method removeMainCategory should call MyBudgetDao.
     *
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryCantBeRemovedException
     */
    @Test
    public void isDaoCalledWhenRemovingExistingMainCategory()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        instance.removeMainCategory(FOOD);
        verify(dao).removeMainCategory(USERNAME, FOOD);
    }

    /**
     * Method removeMainCategory should throw NotFoundException when the category doesn't exist.
     *
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException
     * @throws pl.foltak.mybudget.server.dao.exception.CategoryCantBeRemovedException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenRemovingNonexistingMainCategory()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        doThrow(CategoryNotFoundException.class).when(dao)
                .removeMainCategory(USERNAME, NONEXISTENT);
        instance.removeMainCategory(NONEXISTENT);
    }

    /**
     * Method removeMainCategory should throw BadRequestException when the category can't be
     * removed.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test(expected = BadRequestException.class)
    public void isBadRequestExceptionThrownWhenRemovingMainCategoryWithSubcategories()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        doThrow(CategoryCantBeRemovedException.class).when(dao)
                .removeMainCategory(USERNAME, FOOD);

        instance.removeMainCategory(FOOD);
    }

    /**
     * Method editMainCategory should call MyBudgetDao.
     *
     * @throws CategoryNotFoundException
     */
    @Test
    public void isDaoCalledWhenModifyMainCategory() throws CategoryNotFoundException {
        instance.editMainCategory(FOOD, houseCategory);
        verify(dao).modifyMainCategory(USERNAME, FOOD, houseCategory);
    }

    /**
     * Method editMainCategory should return 200 OK after edited a category.
     */
    @Test
    public void isOkStatusReturnedWhenModifyMainCategory() {
        Response response = instance.editMainCategory(FOOD, houseCategory);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    /**
     * Method editMainCategory should throw NotFoundException when a category with given name
     * doesn't exist.
     *
     * @throws CategoryNotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenModifingNonexistingMainCategory()
            throws CategoryNotFoundException {

        doThrow(CategoryNotFoundException.class).when(dao)
                .modifyMainCategory(any(), any(), any());
        instance.editMainCategory(NONEXISTENT, subCategory);
    }

    /**
     * Method addSubCategory should call MyBudgetDao.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test
    public void isDaoCalledWhenAddingSubCategory()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        instance.addSubCategory(FOOD, houseCategory);
        verify(dao).addSubCategory(USERNAME, FOOD, houseCategory);
    }

    /**
     * Method addSubCategory should return a 201 Created status if a category was added.
     */
    @Test
    public void isCreatedStatusReturnedWhenAddingSubCategory() {
        Response response = instance.addSubCategory(FOOD, houseCategory);
        assertEquals("Status code isn't equal to 201", 201, response.getStatus());
    }

    /**
     * Method addSubCategory should return a location header with the location of new category.
     */
    @Test
    public void isLocationHeaderReturnedWhenAddingSubcategory() {
        Response response = instance.addSubCategory(FOOD, houseCategory);
        assertEquals("Location header isn't equal to new category", createURI(FOOD, HOUSE),
                response.getLocation());
    }

    /**
     * Method addSubCategory should throw NotFoundException when main category doesn't exist.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenCreatingSubcategoryButMainCategoryDoesntExist()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        doThrow(CategoryNotFoundException.class).when(dao)
                .addSubCategory(any(), any(), any());
        instance.addSubCategory(NONEXISTENT, houseCategory);
    }

    /**
     * Method addSubCategory should throw ConflictException when sub category already exists.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = ConflictException.class)
    public void isConflictExceptionThrownWhenAddingSubCategoryThatAlreadyExist()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        doThrow(CategoryAlreadyExistsException.class).when(dao)
                .addSubCategory(any(), any(), any());
        instance.addSubCategory(FOOD, subCategory);
    }

    /**
     * Method removeSubCategory should call MyBudgetDao.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test
    public void isDaoCalledWhenRemovingSubCategory()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        instance.removeSubCategory(FOOD, CANDY);
        verify(dao).removeSubCategory(USERNAME, FOOD, CANDY);
    }

    /**
     * Method removeSubCategory should return status 200 OK, after remove sub category.
     */
    @Test
    public void isOkStatusReturnedAfterRemovingSubCategory() {
        Response response = instance.removeSubCategory(FOOD, CANDY);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    /**
     * Method removeSubCategory should throw NotFoundException when category doesn't exist.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundWhenRemovingSubcategoryAndParentCategoryDoesntExist()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        doThrow(CategoryNotFoundException.class).when(dao)
                .removeSubCategory(any(), any(), any());
        instance.removeSubCategory(NONEXISTENT, CANDY);
    }

    /**
     * Method removeSubCategory should throw BadRequestException when category can't be removed.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryCantBeRemovedException
     */
    @Test(expected = BadRequestException.class)
    public void testThrowBadRequestExceptionWhenRemovingSubcategoryThatHasTransactions()
            throws CategoryNotFoundException, CategoryCantBeRemovedException {

        doThrow(CategoryCantBeRemovedException.class).when(dao)
                .removeSubCategory(any(), any(), any());
        instance.removeSubCategory(FOOD, CANDY);
    }

    /**
     * Method editSubCategory should return 200 OK status after update category.
     */
    @Test
    public void isOkStatusReturnedWhenModifingSubCategory() {
        Response response = instance.editSubCategory(FOOD, CANDY, houseCategory);
        assertEquals("Status code isn't equal to 200 OK", 200, response.getStatus());
    }

    /**
     * Method editSubCategory should call MyBudgetDao.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test
    public void isDaoCalledWhenModifingSubcategory() 
            throws CategoryNotFoundException, CategoryAlreadyExistsException {
        
        instance.editSubCategory(FOOD, CANDY, houseCategory);
        verify(dao).editSubCategory(USERNAME, FOOD, CANDY, houseCategory);
    }

    /**
     * Method editSubCategory should throw NotFoundException when a category doesn't exist.
     *
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenMainCategoryDoesntExist()
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        doThrow(CategoryNotFoundException.class).when(dao)
                .editSubCategory(any(), any(), any(), any());
        instance.editSubCategory(NONEXISTENT, CANDY, houseCategory);
    }

    /**
     * Method editSubCategory should throw ConflictException when a category with given name already exists.
     * 
     * @throws CategoryNotFoundException
     * @throws CategoryAlreadyExistsException
     */
    @Test(expected = ConflictException.class)
    public void isConflictExceptionThrownWhenSubCategoryWithNewNameAlreadyExist() 
            throws CategoryNotFoundException, CategoryAlreadyExistsException {
        
        doThrow(CategoryAlreadyExistsException.class).when(dao)
                .editSubCategory(any(), any(), any(), any());
        instance.editSubCategory(FOOD, CANDY, subCategory);
    }

    /**
     * Method getAllCategories should return 200 OK status.
     */
    @Test
    public void isOkStatusReturnedWhenGettingAllCategories() {
        int statusCode = instance.getAllCategories().getStatus();
        assertEquals("Incorrect status code", 200, statusCode);
    }

    /**
     * Method getAllCategories should return list of categories.
     */
    @Test
    public void isCategoriesListReturnedWhenGettingAllCategories() {
        List<Category> categories = mock(List.class);
        when(dao.getAllCategories(USERNAME)).thenReturn(categories);
        List<Category> result = (List<Category>) instance.getAllCategories().getEntity();
        assertEquals("List of categories it's not equals", categories, result);
    }

    /**
     * Method getSubCategories should return 200 OK status.
     */
    @Test
    public void isOkStatusReturnedWhenGettingSubCategories() {
        final int statusCode = instance.getSubcategories(FOOD).getStatus();
        assertEquals("Incorrect status code", 200, statusCode);
    }

    /**
     * Method getSubCategories should return list of sub categories.
     * 
     * @throws CategoryNotFoundException
     */
    @Test
    public void isSubCategoriesListReturnedWhenGettingSubCategories() 
            throws CategoryNotFoundException {
        List<Category> categories = mock(List.class);
        when(dao.getSubCategories(USERNAME, FOOD)).thenReturn(categories);
        List<Category> result = (List<Category>) instance.getSubcategories(FOOD).getEntity();
        assertEquals("List of categories it's not equals", categories, result);
    }

    /**
     * Method getSubCategories should throw NotFoundException
     * 
     * @throws CategoryNotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void isNotFoundExceptionThrownWhenGettingSubCategoriesFromCategoryThatDoesntExist() 
            throws CategoryNotFoundException {
        
        doThrow(CategoryNotFoundException.class).when(dao)
                .getSubCategories(any(), any());
        instance.getSubcategories(NONEXISTENT);
    }

    private static URI createURI(String firstLevelCategory, String secondLevelCategory) {
        return URI.create("category/" + firstLevelCategory + "/" + secondLevelCategory);
    }

    private static URI createURI(String firstLevelCategory) {
        return URI.create("category/" + firstLevelCategory);
    }

}
