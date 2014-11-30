    package pl.foltak.mybudget.server.security;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class UserAuthenticatorTest {

    private static final String HASH = "$2a$12$3jEVRVePvSgbQ2Y42ilYzOju6iA4YRVzEwWNG6NIf5/jQGPraYgPS";

    private UserAuthenticator instance;

    @Before
    public void setUp() {
        instance = spy(new UserAuthenticator());
        instance.em = mock(EntityManager.class, Mockito.RETURNS_DEEP_STUBS);
    }

    /**
     * Test is true returned, when given password matches to hash stored in user entity.
     */
    @Test
    public void isTrueReturnedWhenGivenPasswordMatchesToHash() {
        doReturn(HASH).when(instance).getPasswordHashForUser("alibaba");

        assertTrue("Authenticate should return true", instance.authenticate("alibaba",
                "fortythieves"));
    }

    /**
     * Test is false returned, when given password doesn't much to hash stored in user entity.
     */
    @Test
    public void isFalseReturnedWhenPasswordDoesntMatchToHash() {
        doReturn(HASH).when(instance).getPasswordHashForUser("alibaba");

        assertFalse("Authenticate should return false", instance.authenticate("alibaba",
                "incorrectPassword"));
    }
    
    /**
     * Tests, is false returned, when password hash is empty string.
     */
    @Test
    public void isFalseReturnedWhenHashIsEmpty() {
        doReturn("").when(instance).getPasswordHashForUser("alibaba");

        assertFalse("Authenticate should return false", instance.authenticate("alibaba",
                "incorrectPassword"));
    }

    /**
     * Test is null returned from getPasswordHashForUser, when user doesn't exist.
     */
    @Test
    public void isNullReturnedWhenUserDoesntExistInGetPasswordHashForUser() {
        when(instance.em.createQuery(UserAuthenticator.SELECT_PASSWORD)
                .setParameter("username", "someone")
                .getSingleResult()).thenThrow(NoResultException.class);
        assertEquals("Method should return empty string", "", instance.getPasswordHashForUser(
                "someone"));
    }

    /**
     * Test is hash returned from getPasswordHashForUser.
     */
    @Test
    public void isHashReturnedFromGetPasswordHashForUser() {
        when(instance.em.createQuery(UserAuthenticator.SELECT_PASSWORD)
                .setParameter("username", "someone")
                .getSingleResult()).thenReturn("somehash");
        assertEquals("Method should return empty string", "somehash", instance.getPasswordHashForUser(
                "someone"));
    }
}
