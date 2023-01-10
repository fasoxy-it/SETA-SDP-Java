package taxi;

import GRPC.GRPCTaxiServer;
import com.sun.jersey.api.client.Client;
import modules.Position;
import modules.Taxi;
import taxi.threads.ReportThread;
import taxi.threads.TaxiInput;
import taxi.threads.WelcomeThread;

import java.sql.Timestamp;
import java.util.Scanner;

public class TaxiProcess {

    private static int id;

    private static String ip;

    private static int port;

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        Client client = Client.create();

        Taxi taxi = new Taxi(id, ip, port);
        taxi.check(client);
        taxi.start(client);

        System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [Taxi] New Taxi created with Id: " + taxi.getId());

        TaxiInput taxiInput = new TaxiInput(taxi);
        taxiInput.start();

        taxi.startPM10Sensor();

        ReportThread reportThread = new ReportThread(taxi);
        GRPCTaxiServer grpcTaxiServer = new GRPCTaxiServer(taxi);

        reportThread.start();
        grpcTaxiServer.start();

        taxi.startSETARideRequestThread();

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
