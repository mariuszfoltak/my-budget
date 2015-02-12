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
import pl.foltak.mybudget.server.entity.Transaction;
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
    public void addAccount(String username, Account account) throws AccountAlreadyExistsException {
        final User user = getUserByName(username);
        if (user.findAccount(account.getName()).isPresent()) {
            throw AccountAlreadyExistsException.of(account.getName());
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
                .orElseThrow(() -> AccountNotFoundException.of(accountName));

        if (user.findAccount(account.getName()).isPresent()) {
            throw AccountAlreadyExistsException.of(account.getName());
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
        Account account = user.findAccount(accountName)
                .orElseThrow(() -> AccountNotFoundException.of(accountName));

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

    /**
     * Adds given category to user with given name. If category already exists, method throws
     * exception.
     *
     * @param username name of user to which category should be added
     * @param category category that should be added
     * @throws CategoryAlreadyExistsException if category already exists
     */
    @Override
    public void addMainCategory(String username, Category category) throws
            CategoryAlreadyExistsException {

        final User user = getUserByName(username);
        if (user.findCategory(category.getName()).isPresent()) {
            throw new CategoryAlreadyExistsException(
                    "Category " + category.getName() + " already exists");
        }
        user.addCategory(category);
    }

    /**
     * Removes given category from an user. Throws an exception if a category doesn't exist or can't
     * be deleted.
     *
     * @param username user which from category should be removed
     * @param categoryName category to be removed
     * @throws CategoryNotFoundException if a category doesn't exist
     * @throws CategoryCantBeRemovedException if a category has transactions or sub categories
     */
    @Override
    public void removeMainCategory(String username, String categoryName) throws
            CategoryNotFoundException, CategoryCantBeRemovedException {

        final User user = getUserByName(username);
        final Category category = user.findCategory(categoryName).orElseThrow(
                () -> CategoryNotFoundException.of(categoryName));

        if (category.hasSubCategories()) {
            throw new CategoryCantBeRemovedException(
                    "Category " + categoryName + " has sub categories");
        }
        if (category.hasTransactions()) {
            throw new CategoryCantBeRemovedException("Categor " + categoryName + "has transactions");
        }
        user.removeCategory(category);
    }

    // TODO: add comment
    @Override
    public void updateMainCategory(String username, String categoryName, Category categoryValues)
            throws CategoryNotFoundException, CategoryAlreadyExistsException {

        User user = getUserByName(username);
        if (!categoryName.equals(categoryValues.getName())
                && user.findCategory(categoryValues.getName()).isPresent()) {
            throw CategoryAlreadyExistsException.of(categoryValues.getName());
        }
        setCategoryFields(user.findCategory(categoryName).orElseThrow(
                () -> CategoryNotFoundException.of(categoryName)), categoryValues);
    }

    /**
     * Adds sub category to parent category.
     *
     * @param username user which to add new sub category
     * @param mainCategoryName main category which to add new sub category
     * @param houseCategory sub category to be added
     * @throws CategoryNotFoundException if main category doesn't exist
     * @throws CategoryAlreadyExistsException if sub category already exists
     */
    @Override
    public void addSubCategory(String username, String mainCategoryName, Category houseCategory) throws
            CategoryNotFoundException, CategoryAlreadyExistsException {
        User user = getUserByName(username);
        Category mainCategory = user.findCategory(mainCategoryName)
                .orElseThrow(() -> CategoryNotFoundException.of(mainCategoryName));
        if (mainCategory.findSubCategory(houseCategory.getName()).isPresent()) {
            throw CategoryAlreadyExistsException.of(houseCategory.getName());
        }
        mainCategory.addSubCategory(houseCategory);
    }

    // TODO: add comment
    @Override
    public void removeSubCategory(String USERNAME, String FOOD, String CANDY) throws
            CategoryNotFoundException, CategoryCantBeRemovedException {

        Category mainCategory = getUserByName(USERNAME).findCategory(FOOD)
                .orElseThrow(() -> CategoryNotFoundException.of(FOOD));

        Category subCategory = mainCategory.findSubCategory(CANDY)
                .orElseThrow(() -> CategoryNotFoundException.of(CANDY));

        if (subCategory.hasTransactions()) {
            // TODO: Write a better message that includes category name
            throw new CategoryCantBeRemovedException("Category has transactions");
        }
        mainCategory.removeSubCategory(subCategory);
    }

    // TODO: add comment
    @Override
    public void updateSubCategory(String username, String mainCategoryName, String subCategoryName,
            Category houseCategory) throws CategoryNotFoundException, CategoryAlreadyExistsException {

        Category mainCategory = getUserByName(username).findCategory(mainCategoryName)
                .orElseThrow(() -> CategoryNotFoundException.of(mainCategoryName));

        Category subCategory = mainCategory.findSubCategory(subCategoryName)
                .orElseThrow(() -> CategoryNotFoundException.of(subCategoryName));

        if (!subCategoryName.equals(houseCategory.getName())
                && mainCategory.findSubCategory(houseCategory.getName()).isPresent()) {
            throw CategoryAlreadyExistsException.of(houseCategory.getName());
        }
        setCategoryFields(subCategory, houseCategory);
    }

    // TODO: add comment
    @Override
    public List<Category> getAllCategories(String username) {
        return getUserByName(username).getCategories();
    }

    // TODO: add comment
    @Override
    public List<Category> getSubCategories(String username, String mainCategory)
            throws CategoryNotFoundException {

        return getUserByName(username).findCategory(mainCategory)
                .orElseThrow(() -> CategoryNotFoundException.of(mainCategory)).getSubCategories();
    }

    // TODO: add comment
    @Override
    public List<Tag> getTags(String username) {
        return getUserByName(username).getTags();
    }

    // TODO: add comment
    @Override
    public void addTransaction(String USERNAME, TransactionDTO transactionDTO) throws
            AccountNotFoundException, CategoryNotFoundException {

        String accountName = transactionDTO.getAccountName();
        String mainCategoryName = transactionDTO.getMainCategoryName();
        String subCategoryName = transactionDTO.getSubCategoryName();

        User user = getUserByName(USERNAME);
        Transaction transaction = convertTransaction(transactionDTO);

        user.findAccount(accountName)
                .orElseThrow(() -> AccountNotFoundException.of(accountName))
                .addTransaction(transaction);

        findSubCategory(user, mainCategoryName, subCategoryName)
                .addTransaction(transaction);

        for (String tagName : transactionDTO.getTags()) {
            Tag tag = findOrCreateTag(tagName);
            transaction.addTag(tag);
        }
    }

    /**
     * Updates transaction using given TransactionDTO object.
     *
     * @param username name of the user to which transactions belongs
     * @param transactionDTO a dto object representing new values of transaction
     *
     * @throws AccountNotFoundException when account doesn't exist
     * @throws TransactionNotFoundException when a transaction with given id doesn't exist
     * @throws CategoryNotFoundException when a category doesn't exist
     */
    @Override
    public void updateTransaction(String username, TransactionDTO transactionDTO) throws
            AccountNotFoundException, TransactionNotFoundException, CategoryNotFoundException {

        User user = getUserByName(username);
        String mainCategoryName = transactionDTO.getMainCategoryName();
        String subCategoryName = transactionDTO.getSubCategoryName();

        Account account = findAccount(user, transactionDTO.getAccountName());
        Category subCategory = findSubCategory(user, mainCategoryName, subCategoryName);
        Transaction transaction = findTransaction(user, transactionDTO.getId());

        updateTransaction(transaction, transactionDTO);
        account.addTransaction(transaction);
        subCategory.addTransaction(transaction);
        updateTags(transaction, transactionDTO);
    }

    private Transaction findTransaction(User user, long id)
            throws TransactionNotFoundException {

        return user.findTransaction(id)
                .orElseThrow(() -> TransactionNotFoundException.of(id));
    }

    private static Category findSubCategory(User user, String mainCategoryName,
            String subCategoryName) throws CategoryNotFoundException {
        return user.findCategory(mainCategoryName)
                .orElseThrow(() -> CategoryNotFoundException.of(mainCategoryName))
                .findSubCategory(subCategoryName)
                .orElseThrow(() -> CategoryNotFoundException.of(subCategoryName));
    }

    private Account findAccount(User user, String accountName)
            throws AccountNotFoundException {

        return user.findAccount(accountName)
                .orElseThrow(() -> AccountNotFoundException.of(accountName));
    }

    private void updateTags(Transaction transaction, TransactionDTO transactionDTO) {
        transaction.clearTags();
        for (String tag : transactionDTO.getTags()) {
            transaction.addTag(findOrCreateTag(tag));
        }
    }

    @Override
    public void removeTransaction(String USERNAME, long ID_47) throws TransactionNotFoundException {
        User user = getUserByName(USERNAME);
        Transaction transaction = user.findTransaction(ID_47)
                .orElseThrow(() -> TransactionNotFoundException.of(ID_47));
        user.removeTransaction(transaction);
    }

    Transaction convertTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setTransactionDate(transactionDTO.getTransactionDate());
        return transaction;
    }

    User getUserByName(String username) {
        TypedQuery<User> query = em.createQuery(SELECT_USER, User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    void setAccountFields(Account account, final Account withValues) {
        account.setName(withValues.getName());
    }

    void setCategoryFields(Category category, Category withValues) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Tag findOrCreateTag(String FIRST_TAG) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void updateTransaction(Transaction transaction, TransactionDTO transactionDTO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
