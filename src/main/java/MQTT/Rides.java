package MQTT;

import java.util.ArrayList;
import java.util.List;

public class Rides {

    private List<Ride> rides = new ArrayList<Ride>();

    public Rides() {}

    public synchronized void addRide(Ride ride) {
        rides.add(ride);
    }

}
