package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import pl.foltak.mybudget.server.dao.MyBudgetDaoLocal;
import pl.foltak.mybudget.server.dao.exception.CategoryAlreadyExistsException;
import pl.foltak.mybudget.server.dao.exception.CategoryCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException;
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

    MyBudgetDaoLocal dao;

    @PUT
    @Path("/")
    public Response addMainCategory(Category category) {
        try {
            getDao().addCategory(getUsername(), category);
        } catch (CategoryAlreadyExistsException ex) {
            throw new ConflictException(String.format(CATEGORY_ALREADY_EXIST, category.getName()),
                    ex);
        }
        return Response.created(createURI(category)).build();
    }

    @POST
    @Path("/{mainCategory}")
    public Response editMainCategory(@PathParam("mainCategory") String categoryName,
            Category category) {
        try {
            getDao().modifyMainCategory(getUsername(), categoryName, category);
        } catch (CategoryNotFoundException ex) {
            throw new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, categoryName), ex);
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/{mainCategory}")
    public Response removeMainCategory(@PathParam("mainCategory") String categoryName) {
        try {
            getDao().removeMainCategory(getUsername(), categoryName);
        } catch (CategoryNotFoundException ex) {
            throw new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, categoryName), ex);
        } catch (CategoryCantBeRemovedException ex) {
            throw new BadRequestException("Category can't be removed", ex);
        }
        return Response.ok().build();
    }

    @PUT
    @Path("/{mainCategory}")
    public Response addSubCategory(@PathParam("mainCategory") String mainCategoryName,
            Category category) {
        try {
            getDao().addSubCategory(getUsername(), mainCategoryName, category);
        } catch (CategoryNotFoundException ex) {
            throw new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, mainCategoryName), ex);
        } catch (CategoryAlreadyExistsException ex) {
            throw new ConflictException(String.format(CATEGORY_ALREADY_EXIST, category.getName()), ex);
        }
        return Response.created(createURI(mainCategoryName, category.getName())).build();
    }

    /**
     * @return the dao
     */
    protected MyBudgetDaoLocal getDao() {
        return dao;
    }

    @POST
    @Path("/{mainCategory}/{subCategory}")
    Response editSubCategory(@PathParam("mainCategory") String mainCategoryName,
            @PathParam("subCategory") String subCategoryName, Category category) {
        
        try {
            getDao().editSubCategory(getUsername(), mainCategoryName, subCategoryName, category);
        } catch (CategoryNotFoundException ex) {
            throw new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, ex.getCategoryName()), ex);
        } catch (CategoryAlreadyExistsException ex) {
            throw new ConflictException(String.format(CATEGORY_ALREADY_EXIST, category.getName()), ex);
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/{mainCategory}/{subCategory}")
    public Response removeSubCategory(@PathParam("mainCategory") String mainCategoryName,
            @PathParam("subCategory") String subCategoryName) {
        try {
            getDao().removeSubCategory(getUsername(), mainCategoryName, subCategoryName);
        } catch (CategoryNotFoundException ex) {
            throw new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, ex.getCategoryName()), ex);
        } catch (CategoryCantBeRemovedException ex) {
            throw new BadRequestException(String.format(CATEGORY_HAS_SUBCATEGORIES, subCategoryName), ex);
        }
        return Response.ok().build();
    }

    @GET
    @Path("/")
    public Response getAllCategories() {
        final List<Category> categories = getDao().getAllCategories(getUsername());
        return Response.ok(categories).build();
    }

    @GET
    @Path("{mainCategory}")
    public Response getSubcategories(@PathParam(value = "mainCategory") String mainCategoryName) {
        final List<Category> subCategories;
        try {
            subCategories = getDao().getSubCategories(getUsername(), mainCategoryName);
        } catch (CategoryNotFoundException ex) {
            throw new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, mainCategoryName), ex);
        }
        return Response.ok(subCategories).build();
    }

    private static URI createURI(String parentCategoryName, String categoryName) {
        return URI.create("category/" + parentCategoryName + "/" + categoryName);
    }

    private static URI createURI(Category category) {
        return URI.create("category/" + category.getName());
    }
}
