package pl.foltak.mybudget.server.entity;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;

/**
 * The account entity.
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class Account {

    List<Transaction> transactions;

    public String getName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setName(String BANK) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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
