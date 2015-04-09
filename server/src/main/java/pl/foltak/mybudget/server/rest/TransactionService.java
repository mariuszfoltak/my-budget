package pl.foltak.mybudget.server.rest;

import java.net.URI;
import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.dao.exception.AccountNotFoundException;
import pl.foltak.mybudget.server.dao.exception.CategoryNotFoundException;
import pl.foltak.mybudget.server.dao.exception.TransactionNotFoundException;
import pl.foltak.mybudget.server.dto.TransactionDTO;

@Path("/transactions")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TransactionService extends AbstractService {
    @PUT
    @Path("/{account}")
    public Response createTransaction(@PathParam("account") String accountName,
            TransactionDTO transactionDTO) {

        try {
            getDao().addTransaction(getUsername(), transactionDTO);
        } catch (AccountNotFoundException | CategoryNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        }
        // TODO: create uriCreator
        return Response.created(URI.create("transaction/" + transactionDTO.getId())).build();
    }

    @POST
    @Path("/{account}")
    public Response modifyTransaction(@PathParam("account") String accountName,
            TransactionDTO transactionDTO) {

        try {
            getDao().updateTransaction(getUsername(), transactionDTO);
        } catch (AccountNotFoundException | TransactionNotFoundException | CategoryNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/")
    public Response removeTransaction(@PathParam("account") String accountName, long transactionId) {
        try {
            getDao().removeTransaction(getUsername(), transactionId);
        } catch (TransactionNotFoundException ex) {
            throw new NotFoundException(ex.getMessage(), ex);
        }
        return Response.ok().build();
    }
}
