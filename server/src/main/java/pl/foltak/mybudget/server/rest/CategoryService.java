package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.User;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Path("/category")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CategoryService {

    private static final String CATEGORY_DOESNT_EXIST = "Category '%s' doesn't exist";
    private static final String CATEGORY_ALREADY_EXIST = "Category '%s' already exists";
    private static final String CATEGORY_HAS_TRANSACTIONS = "Category '%s' has transactions";
    private static final String CATEGORY_HAS_SUBCATEGORIES = "Category '%s' has subcategories";

    User user;

    @PUT
    public Response addMainCategory(Category category) {
        checkIfMainCategoryExists(category);
        user.addCategory(category);
        return Response.created(createURI(category)).build();
    }

    @Path("{mainCategory}")
    @POST
    public Response editMainCategory(@PathParam("mainCategory") String categoryName, Category category) {
        Category currentCategory = findMainCategory(categoryName);
        currentCategory.setName(category.getName());
        return Response.ok().build();
    }

    @Path("{mainCategory}")
    @DELETE
    public Response removeMainCategory(@PathParam("mainCategory") String categoryName) {
        Category category = findMainCategory(categoryName);
        checkIfCategoryHasSubCategories(category);
        checkIfCategoryHasTransactions(category);
        user.removeCategory(categoryName);
        return Response.ok().build();
    }

    @Path("{mainCategory}")
    @PUT
    public Response addSubCategory(@PathParam("mainCategory") String mainCategoryName, Category category) {
        final Category mainCategory = findMainCategory(mainCategoryName);
        checkIfSubCategoryExists(mainCategory, category);
        mainCategory.addCategory(category);
        return Response.created(createURI(mainCategoryName, category.getName())).build();
    }

    @Path("{mainCategory}/{subCategory}")
    @POST
    Response editSubCategory(@PathParam("mainCategory") String mainCategoryName, @PathParam("subCategory") String subCategoryName, Category category) {
        final Category mainCategory = findMainCategory(mainCategoryName);
        final Category subCategory = findSubCategory(mainCategory, subCategoryName);
        checkIfSubCategoryExists(mainCategory, category);
        subCategory.setName(category.getName());
        return Response.ok().build();
    }

    @Path("{mainCategory}/{subCategory}")
    @DELETE
    public Response removeSubCategory(@PathParam("mainCategory") String mainCategoryName, @PathParam("subCategory") String subCategoryName) {
        Category mainCategory = findMainCategory(mainCategoryName);
        Category subCategory = findSubCategory(mainCategory, subCategoryName);
        checkIfCategoryHasTransactions(subCategory);
        mainCategory.removeSubcategory(subCategoryName);
        return Response.ok().build();
    }

    @GET
    public List<Category> getAllCategories(HttpServletResponse response) {
        response.setStatus(200);
        return user.getCategories();
    }

    @Path("{mainCategory}")
    @GET
    public List<Category> getSubcategories(@PathParam("mainCategory") String mainCategoryName, HttpServletResponse response) {
        response.setStatus(200);
        Category mainCategory = findMainCategory(mainCategoryName);
        return mainCategory.getCategories();
    }

    private Category findMainCategory(String categoryName) throws NotFoundException {
        final Category category = user.findCategory(categoryName);
        if (category == null) {
            throw new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, categoryName));
        }
        return category;
    }

    private static URI createURI(String parentCategoryName, String categoryName) {
        return URI.create("category/" + parentCategoryName + "/" + categoryName);
    }

    private static URI createURI(Category category) {
        return URI.create("category/" + category.getName());
    }

    private void checkIfCategoryHasTransactions(Category category) throws BadRequestException {
        if (category.hasTransactions()) {
            throw new BadRequestException(String.format(CATEGORY_HAS_TRANSACTIONS, category.getName()));
        }
    }

    private void checkIfCategoryHasSubCategories(Category category) throws BadRequestException {
        if (category.hasSubCategories()) {
            throw new BadRequestException(String.format(CATEGORY_HAS_SUBCATEGORIES, category.getName()));
        }
    }

    private void checkIfMainCategoryExists(Category category) throws ConflictException {
        if (user.findCategory(category.getName()) != null) {
            throw new ConflictException(String.format(CATEGORY_ALREADY_EXIST, category.getName()));
        }
    }

    private void checkIfSubCategoryExists(final Category mainCategory, Category category) throws ConflictException {
        if (mainCategory.findCategory(category.getName()) != null) {
            throw new ConflictException(String.format(CATEGORY_ALREADY_EXIST, category.getName()));
        }
    }

    private Category findSubCategory(final Category mainCategory, String subCategoryName) throws NotFoundException {
        final Category subCategory = mainCategory.findCategory(subCategoryName);
        if (subCategory == null) {
            throw new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, subCategoryName));
        }
        return subCategory;
    }
}
