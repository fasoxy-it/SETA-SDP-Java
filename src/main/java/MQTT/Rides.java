package MQTT;

import java.util.ArrayList;
import java.util.List;

public class Rides {

    private List<Ride> rides = new ArrayList<Ride>();

    public Rides() {}

    public synchronized void add(Ride ride) {
        rides.add(ride);
    }

    public synchronized int size() { return rides.size(); }

}
