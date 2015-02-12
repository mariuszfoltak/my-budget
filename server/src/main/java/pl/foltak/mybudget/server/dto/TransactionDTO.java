package pl.foltak.mybudget.server.dto;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mfoltak
 */
public class TransactionDTO {
    
    @Setter @Getter private Long id;
    @Setter @Getter private String accountName;
    @Setter @Getter private String mainCategoryName;
    @Setter @Getter private String subCategoryName;

    @Getter @Setter private String description;
    @Getter @Setter private Double amount;
    @Getter @Setter private Date transactionDate;
    
    @Setter @Getter private List<String> tags;

}
