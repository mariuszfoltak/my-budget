package pl.foltak.mybudget.server.dao;

import javax.ejb.Local;
import pl.foltak.mybudget.server.dao.exception.AccountAlreadyExistsException;
import pl.foltak.mybudget.server.dao.exception.AccountCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.AccountNotFoundException;
import pl.foltak.mybudget.server.entity.Account;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Local
public interface MyBudgetDaoLocal {

    public void createAccount(String username, Account bankAccount)
            throws AccountAlreadyExistsException;

    public void updateAccount(String USERNAME, String WALLET, Account bankAccount)
            throws AccountAlreadyExistsException, AccountNotFoundException;

    public void removeAccount(String USERNAME, String WALLET)
            throws AccountNotFoundException, AccountCantBeRemovedException;

    
}
