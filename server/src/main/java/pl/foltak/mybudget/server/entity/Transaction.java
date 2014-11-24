package pl.foltak.mybudget.server.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.NonNull;

/**
 * Transaction entity.
 *
 * @author mariusz@foltak.pl
 */
@Entity
public class Transaction implements Serializable {

    @Id @Getter private Long id;
    private String description;
    private String amount;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date transactionDate;

    @ManyToMany List<Tag> tags;

    public Transaction() {
        tags = new LinkedList<>();
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

    public void clearTags() {
        tags.clear();
    }

}
