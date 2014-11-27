package pl.foltak.mybudget.server.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

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
}
