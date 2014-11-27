package pl.foltak.mybudget.server.security;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Stateless
@LocalBean
public class UserAuthenticator {

    @PersistenceContext
    private EntityManager em;

    public boolean authenticate(String username, String password) {
        String passwordHash = getPasswordHashForUser(username);
        return BCrypt.checkpw(password, passwordHash);
    }

    String getPasswordHashForUser(String username) {
        final String jpql = "SELECT u.passwordHash FROM users AS u WHERE u.username = :username";
        try {
            return (String) em.createQuery(jpql).setParameter("username", username).getSingleResult();
        } catch (NoResultException e) {
            return "";
        }
    }

}
