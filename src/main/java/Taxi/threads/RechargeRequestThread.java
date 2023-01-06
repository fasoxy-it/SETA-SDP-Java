package taxi.threads;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import modules.Position;
import modules.Taxi;

import java.sql.Timestamp;

public class RechargeRequestThread extends Thread {

    Taxi taxi;

    public RechargeRequestThread(Taxi taxi) { this.taxi = taxi; }

    public void run() {

        RechargeLock rechargeLock = new RechargeLock(taxi);

        for (Taxi otherTaxi : taxi.getTaxiList()) {

            // Da sistemare!!! L'algoritmo di Ricart e Agrawala vuole che la richiesta venga effettuata anche a se stesso.

            //if (taxi.getId() != otherTaxi.getId()) {
                RechargeManagementThread rechargeManagementThread = new RechargeManagementThread(taxi, otherTaxi, rechargeLock);
                rechargeManagementThread.start();
            //}

        }

        rechargeLock.block();

    }

}

class RechargeLock {

    public int responses;
    Object lock;
    Taxi taxi;

    public RechargeLock(Taxi taxi) {
        responses = 0; //Deve essere 0
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
        Timestamp timestamp0 = new Timestamp(System.currentTimeMillis());
        System.out.println("Taxi recharging... " + timestamp0);
        taxi.setInCharge(true);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        taxi.setPosition(new Position(0,0)); //Non va bene! Occorre settare la posizione della stazione di ricarica del distretto corrente.
        taxi.setBattery(100);

        System.out.println("Taxi recharged!");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Taxi recharged! " + timestamp);

        taxi.setInCharge(false);
        taxi.setWantCharge(null);
        taxi.rechargeLockServer.wakeUp();
    }

    public void wakeUp() {
        responses++;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
