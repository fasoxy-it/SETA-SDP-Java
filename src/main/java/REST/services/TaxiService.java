package REST.services;

import modules.AddTaxiResponse;
import modules.Position;
import modules.Taxi;
import modules.Taxis;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("taxis")
public class TaxiService {

    @GET
    @Produces("application/json")
    public Response getTaxiList() {
        return Response.ok(Taxis.getInstance().getTaxiList()).build();
    }

    @Path("add")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addTaxi(Taxi taxi) {
        Taxi newTaxi = Taxis.getInstance().add(taxi);
        if (newTaxi != null) {
            AddTaxiResponse addTaxiResponse = new AddTaxiResponse(Taxis.getInstance().getTaxiList(), new Position().getRandomPosition());
            return Response.ok(addTaxiResponse).build();
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

    @Path("get")
    @GET
    @Produces("application/json")
    public Response getTaxi() {
        return Response.ok(Taxis.getInstance().getLastTaxi()).build();
    }

}
