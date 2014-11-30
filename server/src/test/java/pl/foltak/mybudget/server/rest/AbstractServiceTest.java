package pl.foltak.mybudget.server.rest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import pl.foltak.mybudget.server.entity.User;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class AbstractServiceTest {
    
    private AbstractService instance;
    private User user;
    private EntityManager em;
    private TypedQuery query;
    
    @Before
    public void setUp() {
        user = mock(User.class);
        em = mock(EntityManager.class);
        query = mock(TypedQuery.class);
        instance = mock(AbstractService.class);
        instance.emf = mock(EntityManagerFactory.class);
        
        when(instance.getUser()).thenCallRealMethod();
        when(instance.emf.createEntityManager()).thenReturn(em);
        when(em.createQuery(any(String.class), any(Class.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(user);
    }

    /**
     * The method should return user from database, if field user is null.
     */
    @Test
    public void isUserReturnedWhenGetUserIsCalled() {
        
        assertEquals("Incorrect user", user, instance.getUser());
    }
    
    /**
     * The getUser() method should return user from field user.
     */
    @Test
    public void isUserFromFieldReturnedWhenGetUserIsCalled() {
        instance.user = mock(User.class);
        
        assertEquals("Incorrect user", instance.user, instance.getUser());
    }
    
    @Test
    public void doesGetUserAssignUserToFieldOnFirstCall() {
        instance.getUser();
        
        assertEquals("Incorrect user", user, instance.user);
    }
}
