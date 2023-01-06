package taxi.threads;

import MQTT.Ride;
import javafx.geometry.Pos;
import modules.Position;
import modules.Taxi;

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

        System.out.println("Doing the delivery of the order: " + ride.getId());

        taxi.setInRide(true);
        taxi.stopRideRequestThread();
        taxi.setRideList(new ArrayList<>());

        try {

            Thread.sleep(5000);

            double distanceFromStartingPosition = Position.getDistance(taxi.getPosition(), ride.getStartingPosition());
            System.out.println("Taxi arrived to the Starting Position!");
            double distanceFromDestinationPosition = Position.getDistance(ride.getStartingPosition(), ride.getDestinationPosition());
            System.out.println("Taxi arrived to the Destination Position!");
            double distanceRide = distanceFromStartingPosition + distanceFromDestinationPosition;
            System.out.println("Taxi finished the ride!");

            int batteryConsumption = (int) Math.round(distanceRide);

            taxi.setPosition(ride.getDestinationPosition());
            taxi.setDistance(taxi.getDistance() + distanceRide);
            taxi.setBattery(taxi.getBattery() - batteryConsumption);

            taxi.addRides();
            taxi.setInRide(false);

            System.out.println("New position: " + taxi.getPosition());
            System.out.println("New battery level: " + taxi.getBattery());

        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        if (taxi.getBattery() < 90) {
            System.out.println("Taxi needs recharge...");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("Taxi needs recharge... " + timestamp);
            taxi.setWantCharge(String.valueOf(timestamp.getTime()));
            taxi.startRechargeRequestThread();
        } else {
            taxi.startRideRequestThread();
        }

    }

}
