package pl.foltak.mybudget.server.entity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.NonNull;

/**
 * Transaction entity.
 *
 * @author mariusz@foltak.pl
 */
public class Transaction {

    List<Tag> tags;

    public long getId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Returns and unmodifiable list of tags that belongs to this objects.
     *
     * @return list of tags
     */
    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Adds tag to this object. If the tag is null throws {@link java.lang.NullPointerException}.
     *
     * @param tag
     */
    public void addTag(@NonNull Tag tag) {
        tags.add(tag);
    }

}
