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

    private final URICreator uriCreator = new URICreator("categories");

    @PUT
    @Path("/")
    public Response addMainCategory(Category category) {
        try {
            getDao().addMainCategory(getUsername(), category);
        } catch (CategoryAlreadyExistsException ex) {
            throw new ConflictException(ex.getMessage(), ex);
        }
        return Response.created(uriCreator.create(category.getName())).build();
    }

    @POST
    @Path("/{mainCategory}")
    public Response editMainCategory(@PathParam("mainCategory") String categoryName,
            Category category) {
        try {
            getDao().updateMainCategory(getUsername(), categoryName, category);
        } catch (CategoryNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        } catch (CategoryAlreadyExistsException ex) {
            throw new ConflictException(ex.getMessage(), ex);
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/{mainCategory}")
    public Response removeMainCategory(@PathParam("mainCategory") String categoryName) {
        try {
            getDao().removeMainCategory(getUsername(), categoryName);
        } catch (CategoryNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        } catch (CategoryCantBeRemovedException ex) {
            throw new BadRequestException(ex.getMessage(), ex);
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
            throw new NotFoundException(ex.getMessage(), ex);
        } catch (CategoryAlreadyExistsException ex) {
            throw new ConflictException(ex.getMessage(), ex);
        }
        return Response.created(uriCreator.create(mainCategoryName, category.getName())).build();
    }

    @POST
    @Path("/{mainCategory}/{subCategory}")
    public Response editSubCategory(@PathParam("mainCategory") String mainCategoryName,
            @PathParam("subCategory") String subCategoryName, Category category) {

        try {
            getDao().updateSubCategory(getUsername(), mainCategoryName, subCategoryName, category);
        } catch (CategoryNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        } catch (CategoryAlreadyExistsException ex) {
            throw new ConflictException(ex.getMessage(), ex);
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
            throw new NotFoundException(ex.getMessage(), ex);
        } catch (CategoryCantBeRemovedException ex) {
            throw new BadRequestException(ex.getMessage(), ex);
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
            throw new NotFoundException(ex.getMessage(), ex);
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
