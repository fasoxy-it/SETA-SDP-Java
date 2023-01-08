package modules;

import MQTT.Ride;
import MQTT.Rides;
import com.google.gson.JsonArray;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import simulators.PM10Simulator;
import taxi.threads.RechargeLockServer;
import taxi.threads.RechargeRequestThread;
import taxi.threads.RechargeThread;
import taxi.threads.RideRequestThread;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@XmlRootElement
public class Taxi {

    private int id;
    private String ip;
    private int port;

    @JsonIgnore
    private Position position;

    @JsonIgnore
    private double distance;

    @JsonIgnore
    private int battery;

    @JsonIgnore
    private List<Taxi> taxiList;

    @JsonIgnore
    private RideRequestThread rideRequestThread;

    @JsonIgnore
    private List<Ride> rideList;

    @JsonIgnore
    private boolean inRide;

    @JsonIgnore
    private int rides;

    @JsonIgnore
    public RechargeLockServer rechargeLockServer = new RechargeLockServer();

    @JsonIgnore
    private String wantCharge;

    @JsonIgnore
    private boolean inCharge;

    @JsonIgnore
    private RechargeRequestThread rechargeRequestThread;

    @JsonIgnore
    private RechargeThread rechargeThread;

    @JsonIgnore
    private TaxiBuffer pm10Buffer;

    @JsonIgnore
    private PM10Simulator pm10Simulator;

    @JsonIgnore
    private List<Double> pm10Averages;

    public Taxi() {}

    public Taxi(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        battery = 100;
        distance = 0;
        taxiList = new ArrayList<>();
        rideList = new ArrayList<>();
        inRide = false;
        rides = 0;
        wantCharge = null;
        inCharge = false;
        pm10Averages = new ArrayList<>();
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

    public double getDistance() { return distance; }

    public void setDistance(double distance) { this.distance = distance; }

    public void emptyDistance() { this.distance = 0.0; }

    public int getBattery() { return battery; }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public List<Taxi> getTaxiList() { return taxiList; }

    public void setTaxiList(List<Taxi> taxiList) { this.taxiList = taxiList; }

    public synchronized void addTaxiToList(Taxi taxi) {
        this.taxiList.add(taxi);
    }

    public synchronized void removeTaxiFromList(int taxiId) {

        /*for (Taxi taxi: getTaxiList()) {
            if (taxi.getId() == taxiId) {
                taxiList.remove(taxi);
            }
        }*/

        for (Iterator<Taxi> taxiIterator = getTaxiList().listIterator(); taxiIterator.hasNext(); ) {
            if (taxiIterator.next().getId() == taxiId) {
                taxiIterator.remove();
            }
        }
    }

    public void startRideRequestThread() {
        rideRequestThread = new RideRequestThread(this);
        rideRequestThread.start();
    }

    public void startRechargeThread(Ride ride) {
        rechargeThread = new RechargeThread(this, ride);
        rechargeThread.start();
    }

    public void stopRideRequestThread() {
        rideRequestThread.unsubscribe();
    }

    public List<Ride> getRideList() { return rideList; }

    public void setRideList(List<Ride> rideList) { this.rideList = rideList; }

    public synchronized void addRideToList(Ride ride) {
        this.rideList.add(ride);
    }

    public Ride getRide(int rideId) {

        for (Ride ride : rideList) {
            if (ride.getId() == rideId) {
                return ride;
            }
        }
        return null;
    }

    public synchronized boolean getInRide() { return inRide; }

    public synchronized void setInRide(boolean inRide) { this.inRide = inRide; }

    public int getRides() { return rides; }

    public void addRides() { this.rides++; }

    public void emptyRides() { this.rides = 0;}

    public String getWantCharge() { return wantCharge; }

    public void setWantCharge(String wantCharge) { this.wantCharge = wantCharge; }

    public boolean getInCharge() { return inCharge; }

    public void setInCharge(boolean inCharge) { this.inCharge = inCharge; }

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

    public void startPM10Sensor() {
        pm10Buffer = new TaxiBuffer(this);
        pm10Simulator = new PM10Simulator(pm10Buffer);
        pm10Simulator.start();
    }

    public List<Double> getPM10Averages() {
        return this.pm10Averages;
    }

    public void emptyPM10Averages() {
        this.pm10Averages = new ArrayList<>();
    }

    public synchronized void addPM10AverageToPM10Averages(double pm10Average) {
        this.pm10Averages.add(pm10Average);
    }


    public void startRechargeRequestThread() {
        rechargeRequestThread = new RechargeRequestThread(this);
        rechargeRequestThread.start();
    }
}
