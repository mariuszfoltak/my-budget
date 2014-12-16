package pl.foltak.mybudget.server.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * The account entity.
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Entity
public class Account implements Serializable {

    @Id @GeneratedValue private Long id;
    @Setter @Getter private String name;
    
    @OneToMany @JoinColumn(name = "account_id") List<Transaction> transactions;

    /**
     * Return true if this account has transactions, otherwise return false.
     *
     * @return true if this account has transactions, otherwise return false.
     */
    public boolean hasTransactions() {
        return !transactions.isEmpty();
    }

    /**
     * Add transaction to account. If transaction is null, then throw IllegalArgumentException.
     *
     * @param transaction the transaction to be added to the list.
     */
    public void addTransaction(@NonNull Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Remove transaction from account.
     *
     * @param transaction the transaction to be removed from the list.
     */
    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    /**
     * Find and return transaction in this account.
     *
     * @param id transaction id
     * @return transaction wrapped in Optional object.
     */
    public Optional<Transaction> findTransaction(long id) {
        return transactions.stream().filter(e -> e.getId() == id).findAny();
    }
}
