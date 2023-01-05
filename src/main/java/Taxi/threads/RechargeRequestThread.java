package taxi.threads;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import modules.Position;
import modules.Taxi;

public class RechargeRequestThread extends Thread {

    Taxi taxi;

    public RechargeRequestThread(Taxi taxi) { this.taxi = taxi; }

    public void run() {

        RechargeLock rechargeLock = new RechargeLock(taxi);

        for (Taxi otherTaxi : taxi.getTaxiList()) {

            // Da sistemare!!!

            if (taxi.getId() != otherTaxi.getId()) {
                RechargeManagementThread rechargeManagementThread = new RechargeManagementThread(taxi, otherTaxi, rechargeLock);
                rechargeManagementThread.start();
            }

        }

        rechargeLock.block();

    }

}

class RechargeLock {

    public int responses;
    Object lock;
    Taxi taxi;

    public RechargeLock(Taxi taxi) {
        responses = 1; //Deve essere 0
        lock = new Object();
        this.taxi = taxi;
    }

    public void block() {
        synchronized (lock) {
            System.out.println("Waiting");
            while (responses < taxi.getTaxiList().size()) {
                try {
                    lock.wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        System.out.println("Recharging...");
        taxi.setInCharge(true);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        taxi.setPosition(new Position(0,0)); //Non va bene! Occorre settare la posizione della stazione di ricarica del distretto corrente.
        taxi.setBattery(100);
        taxi.setInCharge(false);

        System.out.println("Taxi recharged!");
    }

    public void wakeUp() {
        responses++;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
