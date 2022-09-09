package REST.services;

import REST.beans.Report;
import REST.beans.Reports;

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
        Reports.getInstance().add(report);
        return Response.ok().build();
    }
}
