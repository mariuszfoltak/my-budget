package pl.foltak.mybudget.server.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Transaction entity.
 *
 * @author mariusz@foltak.pl
 */
@Entity(name = "transactions")
public class Transaction implements Serializable {

    @Id @Getter @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Getter @Setter private String description;
    @Getter @Setter private Double amount;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Getter @Setter @Column(name = "transaction_date") private Date transactionDate;

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
