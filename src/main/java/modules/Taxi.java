package modules;

import com.google.gson.JsonArray;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Taxi {

    private int id;
    private String ip;
    private int port;
    @JsonIgnore
    private Position position;

    @JsonIgnore
    private List<Taxi> taxiList;

    public Taxi() {}

    public Taxi(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        taxiList = new ArrayList<>();
    }

    public String toString() { return this.id + " " + this.ip + " " + this.port; }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() { return ip; }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() { return port; }

    public void setPort(int port) {
        this.port = port;
    }

    public Position getPosition() { return position; }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Taxi> getTaxiList() { return taxiList; }

    public void setTaxiList(List<Taxi> taxiList) { this.taxiList = taxiList; }

    public void check(Client client) {

        WebResource webResource = client.resource("http://localhost:1337/taxis/get");

        try {

            ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
            response.getStatus();
            String result = response.getEntity(String.class);
            System.out.println(response);

            setId(Integer.parseInt(result) + 1);
            setIp("localhost");
            setPort(Integer.parseInt(result) + 1);

        } catch (ClientHandlerException clientHandlerException) {
            clientHandlerException.printStackTrace();
        }

    }

    public void start(Client client) {

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

            AddTaxiResponse addTaxiResponse = response.getEntity(AddTaxiResponse.class);
            setTaxiList(addTaxiResponse.getTaxis());
            setPosition(addTaxiResponse.getPosition());

        } catch (ClientHandlerException clientHandlerException) {
            clientHandlerException.printStackTrace();
        }

    }

}
