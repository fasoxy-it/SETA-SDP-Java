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

        ManagerGrpc.ManagerBlockingStub stub = ManagerGrpc.newBlockingStub(channel);

        Definition.RideRequest request = Definition.RideRequest
                .newBuilder()
                .setRideId(ride.getId())
                .setTaxiId(taxi.getId())
                .setTaxiBattery(taxi.getBattery())
                .setDistance(Position.getDistance(taxi.getPosition(), ride.getStartingPosition()))
                .build();

        Definition.RideResponse response = stub.ride(request);

        if (response.getResponse() == true) {
            taxi.getRide(ride.getId()).addCountResponse();
        }

    }

}
