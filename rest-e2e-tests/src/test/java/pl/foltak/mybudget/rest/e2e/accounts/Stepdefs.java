package pl.foltak.mybudget.rest.e2e.accounts;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Stepdefs {

    private final SimpleDao simpleDao = new MySqlSimpleDao();

    private RequestSpecification request;
    private ValidatableResponse response;

    @Given("an \"(.+)\" user account with \"(.+)\" password")
    public void addUserToDatabase(String username, String password) {
        simpleDao.createUser(username, password);
    }
    
    @Before @After
    public void clearDatabase() {
        simpleDao.clearAllTables();
    }

    @When("^I send \"(.+)\" username$")
    public void setUserName(String username) throws Throwable {
        request = given().header("Authorization-User", username);
    }

    @And("\"(.+)\" password")
    public void setPasswordAndSendMessage(String password) throws Throwable {
        response = request.
                header("Authorization-Password", password).
                when().
                get("api/v1/accounts").
                then();
    }

    @Then("^service logged me in$")
    public void checkIsLoginCorrect() throws Throwable {
        response.statusCode(200);
    }

    @Then("^service did not authorize me$")
    public void checkIsResponseUnauthorized() throws Throwable {
        response.statusCode(401);
    }

    @When("^I don't send any credentials$")
    public void sendRequestWithoutCredentials() throws Throwable {
        response = when().get("api/v1/accounts").then();
    }

}
