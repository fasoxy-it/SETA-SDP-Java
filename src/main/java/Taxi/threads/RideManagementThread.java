package taxi.threads;

import MQTT.Ride;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import modules.Position;
import modules.Taxi;
import proto.Definition;
import proto.ManagerGrpc;

import java.sql.Timestamp;

public class RideManagementThread extends Thread {

    Taxi taxi;
    Taxi otherTaxi;
    Ride ride;
    RideLock rideLock;

    public RideManagementThread(Taxi taxi, Taxi otherTaxi, Ride ride, RideLock rideLock) {
        this.taxi = taxi;
        this.otherTaxi = otherTaxi;
        this.ride = ride;
        this.rideLock = rideLock;
    }

    public void run() {

        final ManagedChannel channel = ManagedChannelBuilder.forTarget(otherTaxi.getIp() + ":" + otherTaxi.getPort()).usePlaintext().build();

        ManagerGrpc.ManagerStub stub = ManagerGrpc.newStub(channel);

        Definition.RideRequest request = Definition.RideRequest
                .newBuilder()
                .setRideId(ride.getId())
                .setTaxiId(taxi.getId())
                .setTaxiBattery(taxi.getBattery())
                .setDistance(Position.getDistance(taxi.getPosition(), ride.getStartingPosition()))
                .build();

        System.out.println("[RIDE: " + ride.getId() + "] [SENDER]: Request of riding from: " + taxi.getId() + " to: " + otherTaxi.getId()  + " at timestamp: " + new Timestamp(System.currentTimeMillis()));

        stub.ride(request, new StreamObserver<Definition.RideResponse>() {
            @Override
            public void onNext(Definition.RideResponse rideResponse) {
                System.out.println("[RIDE: " + ride.getId() + "] [RECIVER]: Response of riding from: " + otherTaxi.getId() + " with value of: " + rideResponse.getResponse() + " at timestamp: " + new Timestamp(System.currentTimeMillis()));
                if (rideResponse.getResponse()) {
                    rideLock.wakeUp(true);
                } else {
                    rideLock.wakeUp(false);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                channel.shutdownNow();
                if (throwable.getMessage().equals("UNAVAILABLE: io exception")) {
                    System.err.println("[RIDE: " + ride.getId() + "] " + otherTaxi.getId() + " not responding at timestamp: " + new Timestamp(System.currentTimeMillis()));
                    taxi.removeTaxiFromList(otherTaxi.getId());
                    taxi.startRechargeThread(ride);
                }
            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }
        });

    }


    /*
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
    */

}
