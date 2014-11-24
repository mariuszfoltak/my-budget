package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.rest.exception.ConflictException;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Path("/categories")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CategoryService extends AbstractService {

    private static final String CATEGORY_ALREADY_EXIST = "Category '%s' already exists";
    private static final String CATEGORY_HAS_TRANSACTIONS = "Category '%s' has transactions";
    private static final String CATEGORY_HAS_SUBCATEGORIES = "Category '%s' has subcategories";

    @PUT
    @Path("/")
    public Response addMainCategory(Category category) {
        throwConflictExceptionIfMainCategoryAlreadyExists(category);
        getUser().addCategory(category);
        return Response.created(createURI(category)).build();
    }

    @POST
    @Path("/{mainCategory}")
    public Response editMainCategory(@PathParam("mainCategory") String categoryName,
            Category category) {
        Category currentCategory = findMainCategory(categoryName);
        currentCategory.setName(category.getName());
        return Response.ok().build();
    }

    @DELETE
    @Path("/{mainCategory}")
    public Response removeMainCategory(@PathParam("mainCategory") String categoryName) {
        Category category = findMainCategory(categoryName);
        checkIfCategoryHasSubCategories(category);
        checkIfCategoryHasTransactions(category);
        getUser().removeCategory(category);
        return Response.ok().build();
    }

    @PUT
    @Path("/{mainCategory}")
    public Response addSubCategory(@PathParam("mainCategory") String mainCategoryName,
            Category category) {
        final Category mainCategory = findMainCategory(mainCategoryName);
        throwConflictExceptionIfSubCategoryAlreadyExists(mainCategory, category);
        mainCategory.addSubCategory(category);
        return Response.created(createURI(mainCategoryName, category.getName())).build();
    }

    @POST
    @Path("/{mainCategory}/{subCategory}")
    Response editSubCategory(@PathParam("mainCategory") String mainCategoryName,
            @PathParam("subCategory") String subCategoryName, Category category) {
        final Category mainCategory = findMainCategory(mainCategoryName);
        final Category subCategory = findSubCategory(mainCategory, subCategoryName);
        throwConflictExceptionIfSubCategoryAlreadyExists(mainCategory, category);
        subCategory.setName(category.getName());
        return Response.ok().build();
    }

    @DELETE
    @Path("/{mainCategory}/{subCategory}")
    public Response removeSubCategory(@PathParam("mainCategory") String mainCategoryName,
            @PathParam("subCategory") String subCategoryName) {
        Category mainCategory = findMainCategory(mainCategoryName);
        Category subCategory = findSubCategory(mainCategory, subCategoryName);
        checkIfCategoryHasTransactions(subCategory);
        mainCategory.removeSubCategory(subCategory);
        return Response.ok().build();
    }

    @GET
    @Path("/")
    public Response getAllCategories() {
        final List<Category> categories = getUser().getCategories();
        return Response.ok(categories).build();
    }

    @GET
    @Path("{mainCategory}")
    public Response getSubcategories(@PathParam(value = "mainCategory")
            String mainCategoryName) {
        final Category mainCategory = findMainCategory(mainCategoryName);
        final List<Category> subCategories = mainCategory.getSubCategories();
        return Response.ok(subCategories).build();
    }

    private static URI createURI(String parentCategoryName, String categoryName) {
        return URI.create("category/" + parentCategoryName + "/" + categoryName);
    }

    private static URI createURI(Category category) {
        return URI.create("category/" + category.getName());
    }

    private void checkIfCategoryHasTransactions(Category category) throws BadRequestException {
        if (category.hasTransactions()) {
            throw new BadRequestException(String.format(CATEGORY_HAS_TRANSACTIONS,
                    category.getName()));
        }
    }

    private void checkIfCategoryHasSubCategories(Category category) throws BadRequestException {
        if (category.hasSubCategories()) {
            throw new BadRequestException(String.format(CATEGORY_HAS_SUBCATEGORIES,
                    category.getName()));
        }
    }

    private void throwConflictExceptionIfMainCategoryAlreadyExists(Category category)
            throws ConflictException {
        if (getUser().findCategory(category.getName()).isPresent()) {
            throw new ConflictException(String.format(CATEGORY_ALREADY_EXIST, category.getName()));
        }
    }

    private void throwConflictExceptionIfSubCategoryAlreadyExists(final Category mainCategory,
            Category category) throws ConflictException {
        if (mainCategory.findSubCategory(category.getName()).isPresent()) {
            throw new ConflictException(String.format(CATEGORY_ALREADY_EXIST, category.getName()));
        }
    }
}
