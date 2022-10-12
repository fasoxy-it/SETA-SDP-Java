package REST.services;

import modules.Report;
import modules.Reports;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("reports")
public class ReportService {

    @GET
    @Produces("application/json")
    public Response getReportList() {
        return Response.ok(Reports.getInstance().getReportList()).build();
    }

    @Path("add")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addReport(Report report) {
        System.out.println("Arrivato!");
        Reports.getInstance().add(report);
        return Response.ok().build();
    }

    /*
    @Path("get")
    @GET
    @Produces("application/json")
    public Response getReport() {
        return Response.ok(Reports.getInstance().getLastReport()).build();
    }
    */

}
