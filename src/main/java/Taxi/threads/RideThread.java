package taxi.threads;

import MQTT.Ride;
import MQTT.Rides;
import javafx.geometry.Pos;
import modules.Position;
import modules.Taxi;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public class RideThread extends Thread {

    Taxi taxi;

    Ride ride;

    public RideThread(Taxi taxi, Ride ride) {
        this.taxi = taxi;
        this.ride = ride;
    }

    public void run() {

        System.out.println("[RIDE: " + ride.getId() + "] " + "Doing the ride at the timestamp: " + new Timestamp(System.currentTimeMillis()));

        taxi.setInRide(true);
        taxi.stopRideRequestThread();
        taxi.setRideList(new ArrayList<>());

        try {

            Thread.sleep(5000);

            double distanceFromStartingPosition = Position.getDistance(taxi.getPosition(), ride.getStartingPosition());
            System.out.println("[RIDE: " + ride.getId() + "] " +"Taxi arrived to the Starting Position at the timestamp: " + new Timestamp(System.currentTimeMillis()));
            double distanceFromDestinationPosition = Position.getDistance(ride.getStartingPosition(), ride.getDestinationPosition());
            System.out.println("[RIDE: " + ride.getId() + "] " +"Taxi arrived to the Destination Position at the timestamp: " + new Timestamp(System.currentTimeMillis()));
            double distanceRide = distanceFromStartingPosition + distanceFromDestinationPosition;
            System.out.println("[RIDE: " + ride.getId() + "] " +"Taxi finished the ride at the timestamp: " + new Timestamp(System.currentTimeMillis()));

            int batteryConsumption = (int) Math.round(distanceRide);

            taxi.setPosition(ride.getDestinationPosition());
            taxi.setDistance(taxi.getDistance() + distanceRide);
            taxi.setBattery(taxi.getBattery() - batteryConsumption);

            taxi.addRides();
            taxi.setInRide(false);

            System.out.println("[TAXI] New position: " + taxi.getPosition() + " at the timestamp: " + new Timestamp(System.currentTimeMillis()));
            System.out.println("[TAXI] New battery level: " + taxi.getBattery() + " at the timestamp: " + new Timestamp(System.currentTimeMillis()));

        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        if (taxi.getBattery() < 90) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("[CHARGE] " + "Taxi needs recharge... " + timestamp);
            taxi.setWantCharge(String.valueOf(timestamp.getTime()));
            taxi.startRechargeRequestThread();
        } else {
            taxi.startRideRequestThread();
        }

    }

}
