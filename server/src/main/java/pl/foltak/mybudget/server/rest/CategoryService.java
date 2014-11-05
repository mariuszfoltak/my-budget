package pl.foltak.mybudget.server.rest;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
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

    User user;

    @PUT
    public Response addParentCategory(Category category) {
        if (user.findCategory(category.getName()) != null) {
            throw new ConflictException("Can't create category, because it already exists");
        }
        user.addCategory(category);
        return Response.created(createURI(category)).build();
    }

    @Path("{categoryName}")
    @DELETE
    public Response removeParentCategory(@PathParam("categoryName") String categoryName) {
        final Category category = user.findCategory(categoryName);
        if (category == null) {
            throw new NotFoundException("Can't delete category, because it isn't exist");
        }
        if (category.hasSubCategories()) {
            throw new BadRequestException("Can't remove category, because it has subcategories");
        }
        if (category.hasTransactions()) {
            throw new BadRequestException("Can't remove category, because it has transactions");
        }
        user.removeCategory(categoryName);
        return Response.ok().build();
    }

    @Path("{categoryName}")
    @POST
    public Response editMainCategory(@PathParam("categoryName") String categoryName, Category category) {
        final Category currentCategory = user.findCategory(categoryName);
        if (currentCategory == null) {
            throw new NotFoundException("Can't delete category, because it isn't exist");
        }
        currentCategory.setName(category.getName());
        return Response.ok().build();
    }

    @Path("{parentCategoryName}/{categoryName}")
    @PUT
    public Response addCategory(@PathParam("parentCategoryName") String parentCategoryName, Category category) {
        Category parentCategory = user.findCategory(parentCategoryName);
        if (parentCategory == null) {
            throw new NotFoundException("Can't add category, because parent category doesn't exist");
        }
        if (parentCategory.findCategory(category.getName()) != null) {
            throw new ConflictException("Can't add category, because it already exists");
        }
        parentCategory.addCategory(category);
        return Response.created(createURI(parentCategoryName, category.getName())).build();
    }

    @Path("{parentCategoryName}/{categoryName}")
    @DELETE
    public Response removeSubcategory(String mainCategoryName, String subcategoryName) {
        final Category mainCategory = user.findCategory(mainCategoryName);
        if (mainCategory == null) {
            throw new NotFoundException("Can't delete category, because parent category doesn't exist");
        }
        final Category subCategory = mainCategory.findCategory(subcategoryName);
        if (subCategory == null) {
            throw new NotFoundException("Can't remove category, because it doesn't exist");
        }
        if (subCategory.hasTransactions()) {
            throw new BadRequestException("Can't remove category, because it has transactions");
        }
        mainCategory.removeSubcategory(subcategoryName);
        return Response.ok().build();
    }

    private static URI createURI(String parentCategoryName, String categoryName) {
        return URI.create("category/" + parentCategoryName + "/" + categoryName);
    }

    private static URI createURI(Category category) {
        return URI.create("category/" + category.getName());
    }

    Response editSubcategory(String FOOD, String CANDY, Category category) {
        final Category mainCategory = user.findCategory(FOOD);
        if (mainCategory == null) {
            throw new NotFoundException("Can't modify category, because parent category doesn't exist");
        }
        final Category subCategory = mainCategory.findCategory(CANDY);
        if (subCategory == null) {
            throw new NotFoundException("Can't modify category, because it doesn't exist");
        }
        if (subCategory.hasTransactions()) {
            throw new ConflictException("Can't modify category, because category with new name already exist");
        }
        subCategory.setName(category.getName());
        return Response.ok().build();
    }

    public List<Category> getAllCategories(HttpServletResponse response) {
        response.setStatus(200);
        return user.getCategories();
    }
}
