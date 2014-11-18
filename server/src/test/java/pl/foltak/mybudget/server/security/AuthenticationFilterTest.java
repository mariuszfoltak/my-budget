package pl.foltak.mybudget.server.security;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author mfoltak
 */
public class AuthenticationFilterTest {
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

    private AuthenticationFilter instance;
    private ContainerRequestContext requestContext;
    private UserAuthenticator authenticator;
    private ArgumentCaptor<Response> responseCaptor;

    @Before
    public void setUp() {
        instance = spy(new AuthenticationFilter());
        requestContext = mock(ContainerRequestContext.class, RETURNS_DEEP_STUBS);
        responseCaptor = ArgumentCaptor.forClass(Response.class);
        authenticator = mock(UserAuthenticator.class);
        when(requestContext.getRequest().getMethod()).thenReturn("POST");
        when(requestContext.getHeaderString(AuthenticationFilter.AUTHORIZATION_USERNAME)).thenReturn(USERNAME);
        when(requestContext.getHeaderString(AuthenticationFilter.AUTHORIZATION_PASSWORD)).thenReturn(PASSWORD);
        doReturn(authenticator).when(instance).getUserAuthenticator();
    }

    /**
     * When HTTP method is "options" then filter should do nothing.
     */
    @Test
    public void isFilterDoNothingIfMethodIsOptions() {
        when(requestContext.getRequest().getMethod()).thenReturn("OPTIONS");
        callFilter(requestContext);
        verify(requestContext, never()).abortWith(any());
    }

    /**
     * When username header is not sent, then filter should abort request with
     * Unauthorize status.
     */
    @Test
    public void isRequestAbortedWithUnauthorizedStatusIfUsernameIsNotSent() {
        when(requestContext.getHeaderString(AuthenticationFilter.AUTHORIZATION_USERNAME)).thenReturn(null);
        callFilter(requestContext);
        verify(requestContext).abortWith(responseCaptor.capture());
        assertEquals("Incorrect status code", 401, responseCaptor.getValue().getStatus());
    }

    /**
     * When username header is not sent, then filter should abort request with
     * Unauthorize status.
     */
    @Test
    public void isRequestAbortedWithUnauthorizedStatusIfUsernameHeaderIsEmpty() {
        when(requestContext.getHeaderString(AuthenticationFilter.AUTHORIZATION_USERNAME)).thenReturn("");
        callFilter(requestContext);
        verify(requestContext).abortWith(responseCaptor.capture());
        assertEquals("Incorrect status code", 401, responseCaptor.getValue().getStatus());
    }

    /**
     * When password header is not sent, then filter should abort request with
     * Unauthorized status.
     */
    @Test
    public void isRequestAbortedWithUnauthorizedStatusIfPasswordIsNotSent() {
        when(requestContext.getHeaderString(AuthenticationFilter.AUTHORIZATION_PASSWORD)).thenReturn(null);
        callFilter(requestContext);
        verify(requestContext).abortWith(responseCaptor.capture());
        assertEquals("Incorrect status code", 401, responseCaptor.getValue().getStatus());
    }

    /**
     * When password header is not sent, then filter should abort request with
     * Unauthorized status.
     */
    @Test
    public void isRequestAbortedWithUnauthorizedStatusIfPasswordHeaderIsEmpty() {
        when(requestContext.getHeaderString(AuthenticationFilter.AUTHORIZATION_PASSWORD)).thenReturn("");
        callFilter(requestContext);
        verify(requestContext).abortWith(responseCaptor.capture());
        assertEquals("Incorrect status code", 401, responseCaptor.getValue().getStatus());
    }

    /**
     * When username and password are incorrect, then filter should abort request with
     * Unauthorized status.
     */
    @Test
    public void isRequestAbortedWithUnauthorizedStatusIfUserIsNotAuthenticated() {
        when(authenticator.authenticate(USERNAME, PASSWORD)).thenReturn(Boolean.FALSE);
        callFilter(requestContext);
        verify(requestContext).abortWith(responseCaptor.capture());
        assertEquals("Incorrect status code", 401, responseCaptor.getValue().getStatus());
    }
    
    /**
     * When username and password are correct, then filter should do nothing.
     */
    @Test
    public void isFilterDoNothingIfUserCredentialsAreCorrect() {
        when(authenticator.authenticate(USERNAME, PASSWORD)).thenReturn(Boolean.TRUE);
        callFilter(requestContext);
        verify(requestContext, never()).abortWith(any());
    }

    /**
     * Calls method filter in AuthenticationFilter and intercepts IOException,
     * which should never be thrown in tests.
     *
     * @param requestContext the ContainerRequestContext object for method
     * filter in Filter interface.
     */
    private void callFilter(ContainerRequestContext requestContext) {
        try {
            instance.filter(requestContext);
        } catch (IOException ex) {
            throw new RuntimeException("Something bad happened.");
        }
    }

}
