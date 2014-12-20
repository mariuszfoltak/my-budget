package pl.foltak.mybudget.server.rest;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.foltak.mybudget.server.entity.Tag;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Path("/tags")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TagService extends AbstractService {

    @GET
    @Path("/")
    public Response getTags() {
        List<Tag> tags = getDao().getTags(getUsername());
        return Response.ok(tags).build();
    }

}
