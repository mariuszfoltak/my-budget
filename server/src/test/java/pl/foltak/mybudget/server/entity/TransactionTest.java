package pl.foltak.mybudget.server.entity;

import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test of Transaction entity.
 *
 * @author mariusz@foltak.pl
 */
public class TransactionTest {

    private Transaction instance;

    @Before
    public void setUp() {
        instance = new Transaction();
    }

    /**
     * The getTags() should return a copy of tags list,
     */
    @Test
    public void isReturnedCopyOfTagsListWhenGetTagsIsCalled() {
        instance.tags = new LinkedList<>();
        assertNotSame("List must be a copy", instance.tags, instance.getTags());
    }

    /**
     * The addTag() should add a tag to tags list.
     */
    @Test
    public void isTagAddedToListAddTagMethodIsCalled() {
        final Tag tag = mock(Tag.class);
        instance.tags = mock(List.class);
        instance.addTag(tag);
        verify(instance.tags).add(tag);
    }

    /**
     * The addTag should throw a NullPointerException when parameter is null.
     */
    @Test(expected = NullPointerException.class)
    public void isNullPointerExceptionThrownWhenAddTagWithNullParameterIsCalled() {
        instance.tags = mock(List.class);
        instance.addTag(null);
    }
    
    @Test
    public void isClearedListWhenClearTagsIsCalled() {
        instance.tags = mock(List.class);
        instance.clearTags();
        verify(instance.tags).clear();
    }

}
