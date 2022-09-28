package taxi.threads;

import MQTT.Ride;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import modules.Position;
import modules.Taxi;
import proto.Definition;
import proto.ManagerGrpc;

public class RideManagementThread extends Thread {

    Taxi taxi;
    Taxi otherTaxi;
    Ride ride;

    public RideManagementThread(Taxi taxi, Taxi otherTaxi, Ride ride) {
        this.taxi = taxi;
        this.otherTaxi = otherTaxi;
        this.ride = ride;
    }

    public void run() {

        final ManagedChannel channel = ManagedChannelBuilder.forTarget(otherTaxi.getIp() + ":" + otherTaxi.getPort()).usePlaintext().build();

        ManagerGrpc.ManagerStub stub = ManagerGrpc.newStub(channel);

        Definition.RideRequest request = Definition.RideRequest
                .newBuilder()
                .setId(ride.getId())
                .setDistance(Position.getDistance(taxi.getPosition(), ride.getStartingPosition()))
                .build();

        stub.ride(request, new StreamObserver<Definition.RideResponse>() {

            @Override
            public void onNext(Definition.RideResponse rideResponse) {
                System.out.println(rideResponse.getResponse());

                if (rideResponse.getResponse() == true) {
                    RideThread rideThread = new RideThread(taxi, ride);
                    rideThread.run();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                channel.shutdown();
            }

            @Override
            public void onCompleted() {
                channel.shutdown();
            }

        });

    }

}
