package pl.foltak.mybudget.server.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import pl.foltak.mybudget.server.dao.exception.AccountAlreadyExistsException;
import pl.foltak.mybudget.server.dao.exception.AccountCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.AccountNotFoundException;
import pl.foltak.mybudget.server.dao.exception.CategoryAlreadyExistsException;
import pl.foltak.mybudget.server.dao.exception.CategoryCantBeRemovedException;
import pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException;
import pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException;
import pl.foltak.mybudget.server.dto.TransactionDTO;
import pl.foltak.mybudget.server.entity.Account;
import pl.foltak.mybudget.server.entity.Category;
import pl.foltak.mybudget.server.entity.Tag;
import pl.foltak.mybudget.server.entity.User;

/**
 * DAO service for all entities.
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Stateless
public class MyBudgetDao implements MyBudgetDaoLocal {
    
    private static final String SELECT_USER = "SELECT u FROM users AS u WHERE u.username = :username";

    @PersistenceContext(name = "pl.foltak.my-budget")
    private EntityManager em;

    /**
     * Adds given account to an user with given username. If account with given name already exists,
     * method throws exception.
     *
     * @param username the name of user to which an account should be added
     * @param account an account that should be added
     * @throws AccountAlreadyExistsException when account with given name already exists
     */
    @Override
    public void createAccount(String username, Account account) throws AccountAlreadyExistsException {
        final User user = getUserByName(username);
        if (user.findAccount(account.getName()).isPresent()) {
            throw new AccountAlreadyExistsException();
        }
        user.addAccount(account);
    }

    /**
     * Updates account with given name using values in given account object.
     *
     * @param username the name of user which is owner of an account
     * @param accountName the name of account that should be modified
     * @param account the account object with values that should be used to update account
     * @throws AccountAlreadyExistsException when an account with new name already exists
     * @throws AccountNotFoundException when an account with given name doesn't exists
     */
    @Override
    public void updateAccount(String username, String accountName, Account account)
            throws AccountAlreadyExistsException, AccountNotFoundException {

        final User user = getUserByName(username);
        Account get = user.findAccount(accountName)
                .orElseThrow(AccountNotFoundException::new);

        if (user.findAccount(account.getName()).isPresent()) {
            throw new AccountAlreadyExistsException();
        }

        setAccountFields(get, account);
    }

    /**
     * Removes account from user.
     *
     * @param username the name of user from which an account should be deleted
     * @param accountName the name of account that should be removed
     * @throws AccountNotFoundException when account with given name doesn't exist
     * @throws AccountCantBeRemovedException when the account has transactions
     */
    @Override
    public void removeAccount(String username, String accountName) throws AccountNotFoundException,
            AccountCantBeRemovedException {

        final User user = getUserByName(username);
        Account account = user.findAccount(accountName).orElseThrow(AccountNotFoundException::new);

        if (account.hasTransactions()) {
            throw new AccountCantBeRemovedException();
        }
        user.removeAccount(account);
    }

    /**
     * Returns user accounts list.
     *
     * @param username the name of user which accounts list should be returns
     * @return accounts list
     */
    @Override
    public List<Account> getAccounts(String username) {
        return getUserByName(username).getAccounts();
    }

    @Override
    public void addCategory(String username, Category category) throws
            CategoryAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeMainCategory(String username, String categoryName) throws
            CategoryNotFoundException, CategoryCantBeRemovedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void modifyMainCategory(String username, String categoryName, Category categoryValues) throws
            CategoryNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addSubCategory(String USERNAME, String mainCategoryName, Category houseCategory) throws
            CategoryNotFoundException, CategoryAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSubCategory(String USERNAME, String FOOD, String CANDY) throws
            CategoryNotFoundException, CategoryCantBeRemovedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void editSubCategory(String USERNAME, String FOOD, String CANDY, Category houseCategory) throws
            CategoryNotFoundException, CategoryAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Category> getAllCategories(String username) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Category> getSubCategories(String username, String mainCategory) throws
            CategoryNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Tag> getTags(String username) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTransaction(String USERNAME, TransactionDTO transactionDTO) throws
            AccountNotFoundException, CategoryNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTransaction(String USERNAME, TransactionDTO transactionDTO) throws
            AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeTransaction(String USERNAME, long ID_47) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    User getUserByName(String username) {
        TypedQuery<User> query = em.createQuery(SELECT_USER, User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    void setAccountFields(Account account, final Account withValues) {
        account.setName(withValues.getName());
    }

}
