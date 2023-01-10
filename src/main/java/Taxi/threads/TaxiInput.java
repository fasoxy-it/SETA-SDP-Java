package taxi.threads;

import modules.Log;
import modules.Position;
import modules.Taxi;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Scanner;

public class TaxiInput extends Thread {

    private Taxi taxi;

    public static Scanner scanner = new Scanner(System.in);

    public TaxiInput(Taxi taxi) {
        this.taxi = taxi;
    }

    public void run() {

        while (true) {

            switch (scanner.nextLine()) {

                case "exit":
                    Timestamp timestampExit = new Timestamp(System.currentTimeMillis());
                    System.out.println(Log.ANSI_BLUE + timestampExit +  "exit" + Log.ANSI_RESET);
                    taxi.setWantExit(timestampExit.toString());
                    //System.exit(0);
                    break;

                case "recharge":
                    Timestamp timestampRecharge = new Timestamp(System.currentTimeMillis());
                    System.out.println(Log.ANSI_BLUE + "[" + timestampRecharge + "] [CHARGE] " + "Taxi wants recharge... " + Log.ANSI_RESET);

                    if (!taxi.getInRide()) {
                        taxi.unsubscribeSETARideRequestThread("seta/smartcity/rides/district" + Position.getDistrictFromPosition(taxi.getPosition()));
                        taxi.setWantCharge(String.valueOf(timestampRecharge.getTime()));
                        taxi.startRechargeRequestThread();
                    } else {
                        System.out.println(Log.ANSI_BLUE + "[" + new Timestamp(System.currentTimeMillis()) + "] [CHARGE] " + "Can't recharge now! Taxi is involved in a Ride." + Log.ANSI_RESET);
                    }

                    break;

                default:
                    System.out.println("Inserire un comando valido tra quelli possibili");

            }

        }

    }

}
