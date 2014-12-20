package pl.foltak.mybudget.server.rest;

import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import pl.foltak.mybudget.server.dao.MyBudgetDaoLocal;
import pl.foltak.mybudget.server.entity.Tag;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class TagServiceTest {
    
    private static final String USERNAME = "alibaba";

    private TagService instance;
    private MyBudgetDaoLocal dao;

    @Before
    public void setUp() {
        instance = spy(new TagService());
        dao = mock(MyBudgetDaoLocal.class);

        doReturn(USERNAME).when(instance).getUsername();
        doReturn(dao).when(instance).getDao();
    }

    /**
     * When get tags is called, then service should return 200 OK.
     */
    @Test
    public void isOkStatusReturnedWhenGetTagsIsCalled() {
        int statusCode = instance.getTags().getStatus();
        assertEquals("Incorrect status code", 200, statusCode);
    }

    /**
     * When get tags is called, then the service should return list of tags.
     */
    @Test
    public void doesServiceReturnListOfTagsWhenGetTagsIsCalled() {
        List<Tag> tags = new LinkedList<>();
        tags.add(mock(Tag.class));
        tags.add(mock(Tag.class));
        when(dao.getTags(USERNAME)).thenReturn(tags);

        List<Tag> result = (List<Tag>) instance.getTags().getEntity();
        assertEquals("Incorrect list of tags", tags, result);
    }

}
