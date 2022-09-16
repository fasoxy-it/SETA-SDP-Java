package taxi;

import GRPC.GRPCTaxiServer;
import modules.Taxi;
import taxi.threads.ReportThread;
import taxi.threads.RideThread;
import taxi.threads.WelcomeThread;

import java.util.Iterator;

public class TaxiProcess {

    private static int id;

    private static String ip;

    private static int port;

    public static void main(String[] args) {

        Taxi taxi = new Taxi(id, ip, port);
        taxi.check();
        taxi.start();

        System.out.println(taxi);

        ReportThread reportThread = new ReportThread(taxi);
        RideThread rideThread = new RideThread(taxi);
        GRPCTaxiServer grpcTaxiServer = new GRPCTaxiServer(taxi);

        reportThread.start();
        rideThread.start();
        grpcTaxiServer.start();

        if (taxi.getTaxiList().size() > 1) {

            Iterator<Taxi> iterator = taxi.getTaxiList().iterator();

            while (iterator.hasNext()) {

                Taxi otherTaxi = iterator.next();

                if (!(otherTaxi.getId() == taxi.getId())) {
                    WelcomeThread welcomeThread = new WelcomeThread(taxi, otherTaxi);
                    welcomeThread.run();
                }
            }
        } else {
            System.out.println("Alone Taxi!");
        }

    }

}
