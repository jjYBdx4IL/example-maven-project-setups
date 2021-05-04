package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
@PermitAll
public class Home {

    private static final Logger LOG = LoggerFactory.getLogger(Home.class);

    static {
        LOG.info("static init()");
    }
    
//    @Context
//    UriInfo uriInfo;
//    @Inject
//    public EntityManager em;

    @GET
    @Produces(MediaType.TEXT_PLAIN)   
    public Response get() {
        LOG.info("get()");

        return Response.ok().entity("Hello THERE!").build();
    } 

}
