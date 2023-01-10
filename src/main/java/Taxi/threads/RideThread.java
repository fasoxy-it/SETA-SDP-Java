package taxi.threads;

import MQTT.Ride;
import modules.Log;
import modules.Position;
import modules.Taxi;

import java.sql.Timestamp;

public class RideThread extends Thread {

    Taxi taxi;

    Ride ride;

    public RideThread(Taxi taxi, Ride ride) {
        this.taxi = taxi;
        this.ride = ride;
    }

    public void run() {

        System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [RIDE: " + ride.getId() + "] " + "Doing the ride!");

        taxi.setInRide(true);
        taxi.unsubscribeSETARideRequestThread("seta/smartcity/rides/district" + Position.getDistrictFromPosition(ride.getStartingPosition()));
        //taxi.setRideList(new ArrayList<>());

        try {

            Thread.sleep(5000);

            double distanceFromStartingPosition = Position.getDistance(taxi.getPosition(), ride.getStartingPosition());
            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [RIDE: " + ride.getId() + "] " +"Taxi arrived to the Starting Position!");
            double distanceFromDestinationPosition = Position.getDistance(ride.getStartingPosition(), ride.getDestinationPosition());
            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [RIDE: " + ride.getId() + "] " +"Taxi arrived to the Destination Position!");
            double distanceRide = distanceFromStartingPosition + distanceFromDestinationPosition;
            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [RIDE: " + ride.getId() + "] " +"Taxi finished the ride!");

            int batteryConsumption = (int) Math.round(distanceRide);

            taxi.setPosition(ride.getDestinationPosition());
            taxi.setDistance(taxi.getDistance() + distanceRide);
            taxi.setBattery(taxi.getBattery() - batteryConsumption);

            taxi.addRides();
            taxi.setInRide(false);
            taxi.setWichRide(null);

            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [TAXI] New position: " + taxi.getPosition());
            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [TAXI] New battery level: " + taxi.getBattery());

        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        if (taxi.getBattery() < 90) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println(Log.ANSI_BLUE + "[" + timestamp + "] [CHARGE] " + "Taxi needs recharge... " + Log.ANSI_RESET);
            taxi.setWantCharge(String.valueOf(timestamp.getTime()));
            taxi.startRechargeRequestThread();
        } else {
            taxi.subscribeSETARideRequestThread();
        }

    }

}
