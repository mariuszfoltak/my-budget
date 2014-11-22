package pl.foltak.mybudget.server.entity;

import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Category entity.
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@RequiredArgsConstructor @NoArgsConstructor
@EqualsAndHashCode
public class Category {

    @Getter @Setter @NonNull private String name;
    @Getter @Setter List<Category> subCategories;
    List<Transaction> transactions;

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

}
