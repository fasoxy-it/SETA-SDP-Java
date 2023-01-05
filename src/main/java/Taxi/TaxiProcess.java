package taxi;

import GRPC.GRPCTaxiServer;
import com.sun.jersey.api.client.Client;
import modules.Position;
import modules.Taxi;
import taxi.threads.ReportThread;
import taxi.threads.RideRequestThread;
import taxi.threads.WelcomeThread;

public class TaxiProcess {

    private static int id;

    private static String ip;

    private static int port;

    public static void main(String[] args) {

        Client client = Client.create();

        Taxi taxi = new Taxi(id, ip, port);
        taxi.check(client);
        taxi.start(client);

        System.out.println("New Taxi created with Id: " + taxi.getId());

        taxi.startPM10Sensor();

        ReportThread reportThread = new ReportThread(taxi);
        GRPCTaxiServer grpcTaxiServer = new GRPCTaxiServer(taxi);

        reportThread.start();
        grpcTaxiServer.start();

        taxi.startRideRequestThread();

        for (Taxi otherTaxi : taxi.getTaxiList()) {

            if (!(otherTaxi.getId() == taxi.getId())) {
                WelcomeThread welcomeThread = new WelcomeThread(taxi, otherTaxi);
                welcomeThread.run();
            } else {
                otherTaxi.setPosition(new Position(taxi.getPosition().getX(), taxi.getPosition().getY()));
            }

        }

    }

}
