package pl.foltak.mybudget.rest.e2e.helper;

import java.util.Date;

/**
 *
 * @author mfoltak
 */
public interface SimpleDao {

    public Long createUser(String username, String password);

    public void clearAllTables();

    public Long createAccount(Long userId, String accountName);

    public Long getUserId(String username);

    public Long getAccountId(Long userId, String accountName);

    public Long createCategory(Long userId, String simple);

    public Long createTransaction(Long accountId, Long categoryId, String simple, Double amount, Date date);
    
}
