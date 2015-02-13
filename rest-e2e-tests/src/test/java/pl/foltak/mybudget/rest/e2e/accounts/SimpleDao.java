package pl.foltak.mybudget.rest.e2e.accounts;

/**
 *
 * @author mfoltak
 */
public interface SimpleDao {

    public void createUser(String username, String password);

    public void clearAllTables();
    
}
