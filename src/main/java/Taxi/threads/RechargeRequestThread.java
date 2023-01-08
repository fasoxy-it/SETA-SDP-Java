package taxi.threads;

import modules.Position;
import modules.Taxi;

import java.sql.Timestamp;

public class RechargeRequestThread extends Thread {

    Taxi taxi;

    public RechargeRequestThread(Taxi taxi) { this.taxi = taxi; }

    public void run() {

        RechargeLock rechargeLock = new RechargeLock(taxi);

        for (Taxi otherTaxi : taxi.getTaxiList()) {

                RechargeManagementThread rechargeManagementThread = new RechargeManagementThread(taxi, otherTaxi, rechargeLock);
                rechargeManagementThread.start();

        }

        rechargeLock.block();

    }

}

class RechargeLock {

    public int responses;
    Object lock;
    Taxi taxi;

    public RechargeLock(Taxi taxi) {
        responses = 0;
        lock = new Object();
        this.taxi = taxi;
    }

    public void block() {
        synchronized (lock) {
            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [CHARGE] Waiting...");
            while (responses < taxi.getTaxiList().size()) {
                try {
                    lock.wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        double distanceRide = Position.getDistance(taxi.getPosition(), new Position().getDistrictPosition(String.valueOf(Position.getDistrict(taxi.getPosition()))));

        int batteryConsumption = (int) Math.round(distanceRide);

        taxi.setPosition(new Position().getDistrictPosition(String.valueOf(Position.getDistrict(taxi.getPosition()))));
        taxi.setDistance(taxi.getDistance() + distanceRide);
        taxi.setBattery(taxi.getBattery() - batteryConsumption);

        //System.out.println("[CHARGE] New position: " + taxi.getPosition());
        //System.out.println("[CHARGE] New battery level: " + taxi.getBattery());

        //Timestamp timestampStartRecharging = new Timestamp(System.currentTimeMillis());
        System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [CHARGE] Taxi recharging...");
        taxi.setInCharge(true);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        //System.out.println("[CHARGE] Recharge position: " + taxi.getPosition());
        taxi.setBattery(100);

        //Timestamp timestampFinishRecharging = new Timestamp(System.currentTimeMillis());
        System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [CHARGE] Taxi recharged!");

        taxi.setInCharge(false);
        taxi.setWantCharge(null);
        taxi.rechargeLockServer.wakeUp();
        taxi.startSETARideRequestThread();
    }

    public void wakeUp() {
        responses++;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
