
package pl.foltak.mybudget.server.test;

import static org.junit.Assert.fail;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
public class TestUtils {

    /**
     * Fail test and show message with name of exception that should be thrown.
     * 
     * @param <T> Throwable
     * @param exceptionClass Exception class
     */
    public static <T extends Throwable> void expectedException(Class<T> exceptionClass) {
        fail("Expected exception: " + exceptionClass.getName());
    }
    
}
