package GRPC;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import modules.Taxi;

import java.io.IOException;

public class GRPCTaxiServer extends Thread {

    private Taxi taxi;

    public GRPCTaxiServer(Taxi taxi) {
        this.taxi = taxi;
    }

    @Override
    public void run() {

        try {
            Server server = ServerBuilder.forPort(taxi.getPort())
                    .addService(new ManagerImpl(taxi))
                    .build();

            server.start();

            //System.out.println("Server started!");

            server.awaitTermination();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
