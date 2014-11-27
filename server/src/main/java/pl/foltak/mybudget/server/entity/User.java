package pl.foltak.mybudget.server.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NonNull;

/**
 * The account entity.
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
//TODO: Change table name
@Entity(name = "users")
public class User implements Serializable {

    @Id @Column @GeneratedValue private long id;

    @Column private String username;
    @Getter @Column private String passwordHash;

    @OneToMany
    @JoinColumn(name = "user_id")
    List<Category> categories;
    
    @OneToMany
    @JoinColumn(name = "user_id")
    List<Account> accounts;
    
    @OneToMany
    @JoinColumn(name = "user_id")
    List<Tag> tags;

    /**
     * Adds a category to the User.
     *
     * @param category the category to be added.
     */
    public void addCategory(@NonNull Category category) {
        categories.add(category);
    }

    /**
     * Removes a category from the User.
     *
     * @param category the category to be removed
     */
    public void removeCategory(Category category) {
        categories.remove(category);
    }

    /**
     * Finds a category belongs to user and returns it.
     *
     * @param categoryName name of searched category
     * @return category wrapped in Optional
     */
    public Optional<Category> findCategory(String categoryName) {
        return categories.stream().filter(e -> e.getName().equals(categoryName)).findFirst();
    }

    /**
     * Returns all categories from the user.
     *
     * @return categories list
     */
    public List<Category> getCategories() {
        return new LinkedList<>(categories);
    }

    /**
     * Adds an account to the user.
     *
     * @param account the account to be added
     */
    public void addAccount(@NonNull Account account) {
        accounts.add(account);
    }

    /**
     * Removes an account from the user.
     *
     * @param account the account to be removed
     */
    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    /**
     * Finds an account belongs to the user by the account name and returns it.
     *
     * @param accountName name of searched account
     * @return the account wrapped in Optional
     */
    public Optional<Account> findAccount(String accountName) {
        return accounts.stream().filter(e -> e.getName().equals(accountName)).findFirst();
    }

    /**
     * Returns all accounts belongs to the user.
     *
     * @return list of accounts
     */
    public List<Account> getAccounts() {
        return new LinkedList<>(accounts);
    }

    /**
     * Adds a tag to the user.
     *
     * @param tag the tag to be added
     */
    public void addTag(@NonNull Tag tag) {
        tags.add(tag);
    }

    /**
     * Searches a tag with given name and returns it.
     *
     * @param tagName name of the searched tag
     * @return the tag wrapped in Optional
     */
    public Optional<Tag> findTag(String tagName) {
        return tags.stream().filter(e -> e.getName().equals(tagName)).findFirst();
    }

    /**
     * Returns all tags belongs to the user.
     *
     * @return list of tags
     */
    public List<Tag> getTags() {
        return new LinkedList<>(tags);
    }
}
