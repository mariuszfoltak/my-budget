//package pl.foltak.mybudget.server.dao;
//
//import pl.foltak.mybudget.server.rest.*;
//import java.util.LinkedList;
//import java.util.List;
//import javax.servlet.http.HttpServletResponse;
//import static org.junit.Assert.*;
//import org.junit.Before;
//import org.junit.Test;
//import static org.mockito.Mockito.*;
//import pl.foltak.mybudget.server.entity.Tag;
//import pl.foltak.mybudget.server.entity.User;
//
///**
// *
// * @author Mariusz Foltak <mariusz@foltak.pl>
// */
//public class TagServiceTest {
//
//    private TagService instance;
//    private User user;
//
//    @Before
//    public void setUp() {
//        instance = spy(new TagService());
//        user = mock(User.class);
//
//        doReturn(user).when(instance).getUser();
//    }
//
//    /**
//     * When get tags is called, then service should return 200 OK.
//     */
//    @Test
//    public void isOkStatusReturnedWhenGetTagsIsCalled() {
//        final HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
//        int statusCode = instance.getTags().getStatus();
//        assertEquals("Incorrect status code", 200, statusCode);
//    }
//
//    /**
//     * When get tags is called, then the service should return list of tags.
//     */
//    @Test
//    public void doesServiceReturnListOfTagsWhenGetTagsIsCalled() {
//        List<Tag> tags = new LinkedList<>();
//        tags.add(mock(Tag.class));
//        tags.add(mock(Tag.class));
//        when(user.getTags()).thenReturn(tags);
//
//        List<Tag> result = (List<Tag>) instance.getTags().getEntity();
//        assertEquals("Incorrect list of tags", tags, result);
//    }
//
//}