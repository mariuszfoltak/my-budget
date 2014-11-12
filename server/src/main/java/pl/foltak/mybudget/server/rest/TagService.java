package pl.foltak.mybudget.server.rest;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pl.foltak.mybudget.server.entity.Tag;
import pl.foltak.mybudget.server.entity.User;

/**
 *
 * @author Mariusz Foltak <mariusz@foltak.pl>
 */
@Path("tags")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TagService {
    User user;

    List<Tag> getTags(HttpServletResponse response) {
        response.setStatus(200);
        return user.getTags();
    }

}
