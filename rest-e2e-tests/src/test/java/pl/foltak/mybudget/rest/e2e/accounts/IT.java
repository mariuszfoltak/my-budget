package pl.foltak.mybudget.rest.e2e.accounts;

import static com.jayway.restassured.RestAssured.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mfoltak
 */
public class IT {
    
    @Before
    public void setUp() {
    }

     @Test
     public void hello() {
         expect().statusCode(401).when().get("api/v1/categories");
     }
}
