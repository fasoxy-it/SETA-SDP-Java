package taxi.threads;

import MQTT.Ride;
import modules.Log;
import modules.Taxi;

import java.sql.Timestamp;

public class RideRequestThread extends Thread {

    Taxi taxi;
    Ride ride;

    public RideRequestThread(Taxi taxi, Ride ride) {
        this.taxi = taxi;
        this.ride = ride;
    }

    @Override
    public void run() {

        RideLock rideLock = new RideLock(taxi, ride);

        for (Taxi otherTaxi : taxi.getTaxiList()) {

            RideManagementThread rideManagementThread = new RideManagementThread(taxi, otherTaxi, ride, rideLock);
            rideManagementThread.start();

        }

        rideLock.block();

    }

}

class RideLock {

    public int responses;
    public int responsesTrue;
    Object lock;
    Taxi taxi;
    Ride ride;

    public RideLock(Taxi taxi, Ride ride) {
        responses = 0;
        responsesTrue = 0;
        lock = new Object();
        this.taxi = taxi;
        this.ride = ride;
    }

    public void block() {
        synchronized (lock) {
            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [RIDE: " + ride.getId() + "] Waiting...");
            while (responses < taxi.getTaxiList().size()) {
                try {
                    lock.wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        if (responses == responsesTrue) {

            //System.out.println("Responses is equal to ResponsesTrue");

            if (!taxi.getInRide()) {
                System.out.println(Log.ANSI_GREEN + "[" + new Timestamp(System.currentTimeMillis()) + "] [RIDE: " + ride.getId() + "] Riding assign!" + Log.ANSI_RESET);
                taxi.setInRide(true);
                RideThread rideThread = new RideThread(taxi, ride);
                rideThread.start();
            } else {
                System.out.println(Log.ANSI_RED + "[" + new Timestamp(System.currentTimeMillis()) + "] [RIDE: " + ride.getId() + "] Can't do this ride because I'm already involved in another ride!" + Log.ANSI_RESET);
                // Occorre rilanciare la ride!!!
            }
        } else {
            System.out.println(Log.ANSI_RED + "[" + new Timestamp(System.currentTimeMillis()) + "] [RIDE: " + ride.getId() + "] Riding assign to other!" + Log.ANSI_RESET);
        }


    }

    public void wakeUp(boolean assign) {
        if (assign) {
            responsesTrue++;
        }

        responses++;

        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
