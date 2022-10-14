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
        Reports.getInstance().add(report);
        return Response.ok().build();
    }

    @Path("get/{n}/{t}")
    @GET
    @Produces("application/json")
    public Response getReport(@PathParam("n") int n, @PathParam("t") int t) {
        return Response.ok(Reports.getInstance().getLastNReportsFromReportListForTaxi(n, t)).build();
    }

    @Path("getT/{t1D}+{t1T}/{t2D}+{t2T}")
    @GET
    @Produces("application/json")
    public Response getReport(@PathParam("t1D") String t1D, @PathParam("t1T") String t1T, @PathParam("t2D") String t2D, @PathParam("t2T") String t2T) {
        return Response.ok(Reports.getInstance().getReportsFromReportListBetweenTimestamps(t1D, t1T, t2D, t2T)).build();
    }


}
