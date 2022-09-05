package REST.services;

import REST.beans.Taxi;
import REST.beans.Taxis;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("taxis")
public class TaxiService {

    @GET
    @Produces("application/json")
    public Response getTaxiList() {
        return Response.ok(Taxis.getInstance()).build();
    }

}
