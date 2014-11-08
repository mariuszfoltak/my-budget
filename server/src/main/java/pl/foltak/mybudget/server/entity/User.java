package pl.foltak.mybudget.server.entity;

import java.util.List;
import lombok.Getter;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class User {

    @Getter public List<Category> categories;

    public void addCategory(Category category) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Category findCategory(String categoryName) {
        return categories.stream().filter(a -> a.getName().equals(categoryName)).findAny().get();
    }

    public void removeCategory(String categoryName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addAccount(Account account) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Account findAccount(String wallet) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void removeAccount(Account walletAccount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
