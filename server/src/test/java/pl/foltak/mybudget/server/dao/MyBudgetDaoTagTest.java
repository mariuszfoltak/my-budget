package pl.foltak.mybudget.server.dao;

import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import pl.foltak.mybudget.server.entity.Tag;
import pl.foltak.mybudget.server.entity.User;

/**
 * 
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@RunWith(MockitoJUnitRunner.class)
public class MyBudgetDaoTagTest {

    private static final String USERNAME = "alibaba";

    @Spy
    private MyBudgetDao instance;
    @Mock
    private User user;

    @Before
    public void setUp() {
        doReturn(user).when(instance).getUserByName(USERNAME);
    }

    /**
     * When get tags is called, then MyBudgetDao should return list of tags.
     */
    @Test
    public void doesServiceReturnListOfTagsWhenGetTagsIsCalled() {
        List<Tag> tags = mock(List.class);
        when(user.getTags()).thenReturn(tags);

        assertThat(instance.getTags(USERNAME), is(tags));
    }

}
