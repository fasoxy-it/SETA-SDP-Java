package taxi;

import modules.Taxi;
import taxi.threads.ReportThread;
import taxi.threads.RideThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TaxiProcess {

    private static int id;

    private static String ip;

    private static int port;

    public static void main(String[] args) {

        Taxi taxi = new Taxi(id, ip, port);
        taxi.check();
        taxi.start();

        ReportThread reportThread = new ReportThread(taxi);
        RideThread rideThread = new RideThread(taxi);

        reportThread.start();
        rideThread.start();

    }

}
