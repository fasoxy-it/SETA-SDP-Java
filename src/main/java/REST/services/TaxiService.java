package REST.services;

import REST.beans.Taxi;
import REST.beans.Taxis;

import javax.swing.text.html.parser.Entity;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("taxis")
public class TaxiService {

    @GET
    @Produces("application/json")
    public Response getTaxiList() {
        return Response.ok(Taxis.getInstance()).build();
    }

    @Path("add")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addTaxi(Taxi taxi) {
        Taxi newTaxi = Taxis.getInstance().add(taxi);
        if (newTaxi != null) {
            //return Response.ok().build();
            return Response.ok(Taxis.getInstance().getTaxiList()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Path("remove/{taxiId}")
    @DELETE
    @Consumes({"application/json"})
    public Response removeTaxi(@PathParam("taxiId") int taxiId) {
        Taxis.getInstance().remove(taxiId);
        return Response.ok().build();
    }

}
