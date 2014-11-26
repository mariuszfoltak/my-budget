package pl.foltak.mybudget.server.rest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.User;
import pl.foltak.mybudget.server.security.AuthenticationFilter;

/**
 *
 * @author mfoltak
 */
public abstract class AbstractService {

    protected static final String CATEGORY_DOESNT_EXIST = "Category '%s' doesn't exist";
    
    @PersistenceUnit
    private EntityManagerFactory emf;
    
//    TODO: Move AUTHORIZATION_USERNAME to more specific class
    @HeaderParam(value = AuthenticationFilter.AUTHORIZATION_USERNAME)
    private String username;

    /**
     * @return the user
     */
    protected User getUser() {
//        TODO: Shold be loaded only on first call, and maybe can look clearer.
//        TODO: User should be loaded once per request
        EntityManager entityManager = emf.createEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> qUser = cq.from(User.class);
        cq.where(cb.equal(qUser.get("username"), username));
        TypedQuery<User> query = entityManager.createQuery(cq);
        final User singleResult = query.getSingleResult();
        return singleResult;
    }

    protected Category findMainCategory(String categoryName) throws NotFoundException {
        return getUser().findCategory(categoryName).orElseThrow(() -> {
            return new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, categoryName));
        });
    }

    protected Category findSubCategory(Category mainCategory, String subCategoryName)
            throws NotFoundException {
        return mainCategory.findSubCategory(subCategoryName).orElseThrow(() -> {
            return new NotFoundException(String.format(CATEGORY_DOESNT_EXIST, subCategoryName));
        });
    }

    protected Account findAccount(String wallet) throws NotFoundException {
        return getUser().findAccount(wallet).orElseThrow(() -> {
            return new NotFoundException(String.format(ACCOUNT_DOESNT_EXIST, wallet));
        });
    }
    private static final String ACCOUNT_DOESNT_EXIST = "Account '%s' doesn't exist";

}
