
package pl.foltak.mybudget.server.rest;

import static org.junit.Assert.fail;

/**
 *
 * @author Mariusz Foltak <mariusz.foltak@coi.gov.pl>
 */
public class TestUtils {

    static <T extends Throwable> void expectedException(Class<T> exceptionClass) {
        fail("Expected exception: " + exceptionClass.getName());
    }
    
}
