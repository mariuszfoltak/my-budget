package pl.foltak.mybudget.server.entity;

import java.util.List;
import java.util.Optional;

/**
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

    public boolean hasTransactions() {
        return !transactions.isEmpty();
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("The transaction parameter can't be null");
        }
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public Optional<Transaction> findTransaction(long id) {
        return transactions.stream().filter(e -> e.getId() == id).findAny();
    }
}
