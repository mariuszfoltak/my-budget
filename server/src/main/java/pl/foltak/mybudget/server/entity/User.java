package pl.foltak.mybudget.server.entity;

import java.util.List;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class User {

    public List<Category> categories;

    public void addCategory(Category category) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Category findCategory(String categoryName) {
        return categories.stream().filter(a -> a.getName().equals(categoryName)).findAny().get();
    }

    public void removeCategory(String categoryName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
