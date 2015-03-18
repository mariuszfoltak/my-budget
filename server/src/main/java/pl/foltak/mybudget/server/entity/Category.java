package pl.foltak.mybudget.server.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Category entity.
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Entity(name = "categories")
public class Category implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

    @Getter @Setter private String name;

    @OneToMany
    @JoinColumn(name = "parent_id")
    List<Category> subCategories;

    @OneToMany
    @JoinColumn(name = "category_id")
    List<Transaction> transactions;

    public Category() {
        this.subCategories = new LinkedList<>();
    }

    /**
     * Adds subcategory to this object. If subCategory is null then throw
     * {@link java.lang.NullPointerException}.
     *
     * @param subCategory the category object
     */
    public void addSubCategory(@NonNull Category subCategory) {
        subCategories.add(subCategory);
    }

    /**
     * Searches for category with given name and returns it.
     *
     * @param subCategoryName name of searched category
     * @return category wrapped in Optional objects
     */
    public Optional<Category> findSubCategory(String subCategoryName) {
        return subCategories.stream().filter(e -> e.getName().equals(subCategoryName)).findFirst();
    }

    /**
     * Check if this object has any subCategories.
     *
     * @return true if has subcategories, otherwise false
     */
    public boolean hasSubCategories() {
        return !subCategories.isEmpty();
    }

    /**
     * Removes subCategory from this object.
     *
     * @param subCategory the category object that should be removed
     */
    public void removeSubCategory(Category subCategory) {
        subCategories.remove(subCategory);
    }

    /**
     * Adds transaction to this object. If transaction is null then
     * {@link java.lang.NullPointerException} is thrown.
     *
     * @param transaction transaction to be added.
     */
    public void addTransaction(@NonNull Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Check if this object has any transactions.
     *
     * @return true if has transactions, otherwise falses
     */
    public boolean hasTransactions() {
        return !transactions.isEmpty();
    }

    /**
     * Returns a list of subcategories.
     *
     * @return list of subcategories.
     */
    @XmlTransient
    public List<Category> getSubCategories() {
        return new LinkedList<>(subCategories);
    }
}
