package pl.foltak.mybudget.server.rest;

import javax.ejb.EJB;
import javax.ws.rs.HeaderParam;
import lombok.Getter;
import pl.foltak.mybudget.server.dao.MyBudgetDaoLocal;
import pl.foltak.mybudget.server.security.AuthenticationFilter;

/**
 *
 * @author mfoltak
 */
public abstract class AbstractService {

//    TODO: Move AUTHORIZATION_USERNAME to more specific class
    @HeaderParam(value = AuthenticationFilter.AUTHORIZATION_USERNAME)
    @Getter private String username;

    @EJB
    @Getter private MyBudgetDaoLocal dao;
}
