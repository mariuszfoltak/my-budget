
package pl.foltak.mybudget.server.rest.exception;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mariusz Foltak <mariusz.foltak@coi.gov.pl>
 */
public class ConflictException extends ClientErrorException {

    public ConflictException(String message) {
        super(message, Response.Status.CONFLICT);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, Response.Status.CONFLICT, cause);
    }
}
