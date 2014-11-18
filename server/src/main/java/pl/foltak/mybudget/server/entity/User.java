package pl.foltak.mybudget.server.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class User {

    List<Category> categories;
    List<Account> accounts;
    List<Tag> tags;

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }

    public Optional<Category> findCategory(String categoryName) {
        return categories.stream().filter(e -> e.getName().equals(categoryName)).findFirst();
    }

    public List<Category> getCategories() {
        return new LinkedList<>(categories);
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    public Optional<Account> findAccount(String accountName) {
        return accounts.stream().filter(e -> e.getName().equals(accountName)).findFirst();
    }

    public List<Account> getAccounts() {
        return new LinkedList<>(accounts);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public Optional<Tag> findTag(String tagName) {
        return tags.stream().filter(e->e.getName().equals(tagName)).findFirst();
    }

    public List<Tag> getTags() {
        return new LinkedList<>(tags);
    }
}
