/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.foltak.mybudget.server.security;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

/**
 *
 * @author mfoltak
 */
public class AuthenticationFilter implements ContainerRequestFilter {

    static final String AUTHORIZATION_PASSWORD = "Authorization-Password";
    static final String AUTHORIZATION_USERNAME = "Authorization-User";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (requestContext.getRequest().getMethod().equals("OPTIONS")) {
            return;
        }
        final String username = requestContext.getHeaderString(AUTHORIZATION_USERNAME);
        final String password = requestContext.getHeaderString(AUTHORIZATION_PASSWORD);
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        if (!getUserAuthenticator().authenticate(username, password)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    UserAuthenticator getUserAuthenticator() {
        return new UserAuthenticator();
    }

}
