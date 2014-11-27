package pl.foltak.mybudget.server.security;

import java.io.IOException;
import javax.ejb.EJB;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * The authorization filter.
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    public static final String AUTHORIZATION_PASSWORD = "Authorization-Password";
    public static final String AUTHORIZATION_USERNAME = "Authorization-User";
    
    @EJB
    private UserAuthenticator userAuthenticator;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String username = requestContext.getHeaderString(AUTHORIZATION_USERNAME);
        final String password = requestContext.getHeaderString(AUTHORIZATION_PASSWORD);

        if (requestContext.getRequest().getMethod().equals("OPTIONS")) {
            return;
        }
        
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        
        if (!getUserAuthenticator().authenticate(username, password)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    UserAuthenticator getUserAuthenticator() {
        return userAuthenticator;
    }

}
