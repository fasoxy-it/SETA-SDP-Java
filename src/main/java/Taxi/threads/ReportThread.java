package taxi.threads;

import modules.Taxi;
import taxi.TaxiProcess;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ReportThread extends Thread {

    private Taxi taxi;

    public ReportThread(Taxi taxi) {
        this.taxi = taxi;
    }

    @Override
    public void run() {

        while (true) {

            System.out.println("Distance: " + taxi.getDistance() + ", Battery: " + taxi.getBattery() + ", Rides: " + taxi.getRides());

            try {
                Thread.sleep(15000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            /*

            Client client = Client.create();
            JSONObject payload = new JSONObject();

            try {

                payload.put("taxi", taxi.getId());
                payload.put("battery", 100);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            WebResource webResource = client.resource("http://localhost:1337/reports/add");

            try {

                ClientResponse response = webResource.type("application/json").post(ClientResponse.class, payload);
                response.getStatus();
                System.out.println(response);

                Thread.sleep(15000);

            } catch (ClientHandlerException clientHandlerException) {
                clientHandlerException.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

             */

        }



    }

}
