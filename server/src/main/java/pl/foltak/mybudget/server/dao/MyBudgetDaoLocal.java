package pl.foltak.mybudget.server.dao;

import java.util.List;
import javax.ejb.Local;
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

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Local
public interface MyBudgetDaoLocal {

    public void createAccount(String username, Account account)
            throws AccountAlreadyExistsException;

    public void updateAccount(String username, String accountName, Account account)
            throws AccountAlreadyExistsException, AccountNotFoundException;

    public void removeAccount(String username, String accountName)
            throws AccountNotFoundException, AccountCantBeRemovedException;

    public List<Account> getAccounts(String username);

    public void addCategory(String username, Category category) throws CategoryAlreadyExistsException;

    public void removeMainCategory(String username, String categoryName) 
            throws CategoryNotFoundException, CategoryCantBeRemovedException;

    public void modifyMainCategory(String username, String categoryName, Category categoryValues)
            throws CategoryNotFoundException;

    public void addSubCategory(String USERNAME, String mainCategoryName, Category houseCategory)
            throws CategoryNotFoundException, CategoryAlreadyExistsException;

    public void removeSubCategory(String USERNAME, String FOOD, String CANDY)
            throws CategoryNotFoundException, CategoryCantBeRemovedException;

    public void editSubCategory(String USERNAME, String FOOD, String CANDY, Category houseCategory)
            throws CategoryNotFoundException, CategoryAlreadyExistsException;

    public List<Category> getAllCategories(String username);
    
    public List<Category> getSubCategories(String username, String mainCategory)
            throws CategoryNotFoundException;

    public List<Tag> getTags(String username);

    public void addTransaction(String USERNAME, TransactionDTO transactionDTO)
            throws AccountNotFoundException, CategoryNotFoundException;

    public void updateTransaction(String USERNAME, TransactionDTO transactionDTO)
            throws AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException;

    public void removeTransaction(String USERNAME, long ID_47) throws TransactionNotFoundException;

}
