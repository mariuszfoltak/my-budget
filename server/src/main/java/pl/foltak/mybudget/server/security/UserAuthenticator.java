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

    static final String SELECT_PASSWORD = "SELECT u.passwordHash FROM users AS u WHERE u.username = :username";

    @PersistenceContext
    EntityManager em;

    public boolean authenticate(String username, String password) {
        String passwordHash = getPasswordHashForUser(username);
        if ("".equals(passwordHash)) {
            return false;
        }
        return BCrypt.checkpw(password, passwordHash);
    }

    String getPasswordHashForUser(String username) {
        try {
            return (String) em.createQuery(SELECT_PASSWORD).setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return "";
        }
    }

}
