package taxi;

import taxi.threads.ReportThread;
import taxi.threads.RideThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Taxi {

    private static int id;

    private static String ip;

    private static int port;

    public Taxi(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public static void main(String[] args) {

        Taxi taxi = new Taxi(id, ip, port);
        taxi.check();
        taxi.start();

        ReportThread reportThread = new ReportThread(taxi);
        RideThread rideThread = new RideThread(taxi);

        reportThread.start();
        rideThread.start();

    }

    public int getId() { return id; }

    public void check() {

        Client client = Client.create();
        WebResource webResource = client.resource("http://localhost:1337/taxis/get");

        try {

            ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
            response.getStatus();
            String result = response.getEntity(String.class);
            System.out.println(response);

            id = Integer.parseInt(result) + 1;
            ip = "localhost";
            port = Integer.parseInt(result) + 1;

        } catch (ClientHandlerException clientHandlerException) {
            clientHandlerException.printStackTrace();
        }

    }

    public void start() {

        Client client = Client.create();
        JSONObject payload = new JSONObject();

        try {

            payload.put("id", id);
            payload.put("ip", ip);
            payload.put("port", port);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        WebResource webResource = client.resource("http://localhost:1337/taxis/add");

        try {
            ClientResponse response = webResource.type("application/json").post(ClientResponse.class, payload);
            response.getStatus();
            System.out.println(response);
        } catch (ClientHandlerException clientHandlerException) {
            clientHandlerException.printStackTrace();
        }

    }

}
