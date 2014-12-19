package pl.foltak.mybudget.server.rest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
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
    private static final String SELECT_USER = "SELECT u FROM users AS u WHERE u.username = :username";
    protected static final String ACCOUNT_DOESNT_EXIST = "Account '%s' doesn't exist";

//    TODO: Move AUTHORIZATION_USERNAME to more specific class
    @HeaderParam(value = AuthenticationFilter.AUTHORIZATION_USERNAME)
    String username;

    @PersistenceUnit
    EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;

    User user;

    /**
     * @return the user
     */
    protected User getUser() {
        if (user == null) {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            TypedQuery<User> query = em.createQuery(SELECT_USER, User.class);
            query.setParameter("username", getUsername());
            user = query.getSingleResult();
        }
        return user;
    }

    protected void closeEntityManager() {
        tx.commit();
        em.close();
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

    /**
     * @return the username
     */
    protected String getUsername() {
        return username;
    }

}
