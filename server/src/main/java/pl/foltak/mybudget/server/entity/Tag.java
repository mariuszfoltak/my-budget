package pl.foltak.mybudget.server.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;

/**
 *
 * @author Mariusz Foltak <mariusz.foltak@coi.gov.pl>
 */
@Entity(name = "tags")
public class Tag implements Serializable {

    @Id
    private Long id;

    @Getter private String name;
}
