package pl.foltak.mybudget.rest.e2e.accounts;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.util.Date;
import java.util.List;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
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

    @Given("^\"(.*?)\" user with accounts: (.+)$")
    public void createUserWithAccounts(String username, List<String> accounts) {
        Long userId = simpleDao.createUser(username, username);
        accounts.stream().forEach((accountName) -> simpleDao.createAccount(userId, accountName));
    }

    @Given("^I am \"(.*?)\" user$")
    public void setUsername(String username) {
        this.username = username;
    }

    @When("^I add \"(.*?)\" account$")
    public void addAccount(String accountName) {
        response = given()
                .body("{\"name\": \"" + accountName + "\"}")
                .put("/accounts")
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

    enum HttpStatusCode {

        OK("OK", 200),
        CREATED("Created", 201),
        BAD_REQUEST("Bad Request", 400),
        UNAUTHORIZED("Unauthorized", 401),
        NOT_FOUND("Not Found", 404),
        CONFLICT("Conflict", 409),;

        private final String name;
        private final Integer code;

        private HttpStatusCode(String name, Integer code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public Integer getCode() {
            return code;
        }

        public static HttpStatusCode fromName(String name) {
            for (int i = 0; i < values().length; i++) {
                HttpStatusCode httpStatusCode = values()[i];
                if (httpStatusCode.name.equals(name)) {
                    return httpStatusCode;
                }
            }
            throw new IllegalArgumentException("Enum with name " + name + " doesn't exist");
        }

    }

    @Then("^I have (\\d+) accounts: (.+)$")
    public void checkAccounts(int count, List<String> accountsNames) throws Throwable {
        given()
                .get("/accounts")
                .then()
                .body("accounts", hasSize(count))
                .and()
                .body("name", hasItems(accountsNames.toArray()));
    }

    @When("^I change name \"(.*?)\" account to \"(.*?)\"$")
    public void changeAccountName(String fromAccountName, String toAccountName) throws Throwable {
        response = given()
                .body(String.format("{\"name\":\"%s\"}", toAccountName))
                .post("/accounts/{accountName}", fromAccountName)
                .then();
    }

    @When("^I remove \"(.*?)\" account$")
    public void removeAccount(String accountName) throws Throwable {
        response = given()
                .delete("/accounts/{accountName}", accountName)
                .then();
    }

    @Given("^an (.*?) account has transactions$")
    public void an_wallet_account_has_transactions(String accountName) throws Throwable {
        Long userId = simpleDao.getUserId(username);
        Long accountId = simpleDao.getAccountId(userId, accountName);
        Long categoryId = simpleDao.createCategory(userId, "simple");
        simpleDao.createTransaction(accountId, categoryId, "simple", 10.0, new Date());
    }

}
