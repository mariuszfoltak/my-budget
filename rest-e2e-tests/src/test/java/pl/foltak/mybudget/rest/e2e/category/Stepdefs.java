package pl.foltak.mybudget.rest.e2e.category;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.util.Date;
import java.util.List;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import pl.foltak.mybudget.rest.e2e.helper.HttpStatusCode;
import pl.foltak.mybudget.rest.e2e.helper.MySqlSimpleDao;
import pl.foltak.mybudget.rest.e2e.helper.SimpleDao;

public class Stepdefs {

    private final SimpleDao simpleDao = new MySqlSimpleDao();
    private String username;
    private ValidatableResponse response;

    @Before @After
    public void clearDatabase() {
        simpleDao.clearAllTables();
    }

    @Given("^\"(.*?)\" user with categories: (.+)$")
    public void createUserWithCategories(String username, List<String> categories) {
        Long userId = simpleDao.createUser(username, username);
        categories.stream().forEach((categoryName) -> simpleDao.createCategory(userId, categoryName));
    }

    @Given("^I am \"(.*?)\" user$")
    public void setUsername(String username) {
        this.username = username;
    }

    @When("^I add \"(.*?)\" category$")
    public void addAccount(String categoryName) {
        response = given()
                .body("{\"name\": \"" + categoryName + "\"}")
                .put("/categories")
                .then();
    }

    private RequestSpecification given() {
        return RestAssured.given()
                .basePath("api/v1")
                .header("Authorization-User", username)
                .header("Authorization-Password", username)
                .contentType("application/json");
    }

    @Then("^I receive HTTP (.*?) status$")
    public void checkHttpStatus(String status) {
        response.statusCode(HttpStatusCode.fromName(status).getCode());
    }


    @Then("^I have (\\d+) categories: (.+)$")
    public void checkCategories(int count, List<String> categoriesNames) throws Throwable {
        given()
                .get("/categories")
                .then()
                .body("", hasSize(count))
                .and()
                .body("name", hasItems(categoriesNames.toArray()));
    }

    @When("^I change name \"(.*?)\" category to \"(.*?)\"$")
    public void changeAccountName(String fromCategoryName, String toCategoryName) throws Throwable {
        response = given()
                .body(String.format("{\"name\":\"%s\"}", toCategoryName))
                .post("/categories/{categoryName}", fromCategoryName)
                .then();
    }

    @When("^I remove \"(.*?)\" category$")
    public void removeAccount(String categoryName) throws Throwable {
        response = given()
                .delete("/categories/{categoryName}", categoryName)
                .then();
    }

    @Given("^an (.*?) category has transactions$")
    public void an_wallet_account_has_transactions(String categoryName) throws Throwable {
        Long userId = simpleDao.getUserId(username);
        Long categoryId = simpleDao.createCategory(userId, categoryName);
        Long accountId = simpleDao.createAccount(userId, "simple");
        simpleDao.createTransaction(accountId, categoryId, "simple", 10.0, new Date());
    }

}
