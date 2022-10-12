package taxi.threads;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import modules.Report;
import modules.Taxi;
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

            Client client = Client.create();

            Gson gson = new Gson();

            Report report = new Report(taxi.getId(), taxi.getPM10Averages(), taxi.getRides(), taxi.getDistance(), taxi.getBattery());

            String payload = gson.toJson(report);

            WebResource webResource = client.resource("http://localhost:1337/reports/add");

            try {

                ClientResponse response = webResource.type("application/json").post(ClientResponse.class, payload);
                response.getStatus();
                System.out.println(response);

                taxi.emptyPM10Averages();

                Thread.sleep(15000);

            } catch (ClientHandlerException clientHandlerException) {
                clientHandlerException.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }



    }

}
