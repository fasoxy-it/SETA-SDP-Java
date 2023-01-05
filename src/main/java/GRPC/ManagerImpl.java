package GRPC;

import MQTT.Ride;
import io.grpc.stub.StreamObserver;
import modules.Position;
import modules.Taxi;
import proto.Definition;
import proto.ManagerGrpc;

public class ManagerImpl extends ManagerGrpc.ManagerImplBase {

    Taxi taxi;

    public ManagerImpl(Taxi taxi) {
        this.taxi = taxi;
    }

    @Override
    public void welcome(Definition.WelcomeMessage request, StreamObserver<Definition.WelcomeResponse> responseStreamObserver) {

        Definition.WelcomeResponse response = Definition.WelcomeResponse
                .newBuilder()
                .setId(taxi.getId())
                .setPosition(Definition.Position
                        .newBuilder()
                        .setX(taxi.getPosition().getX())
                        .setY(taxi.getPosition().getY())
                        .build()
                )
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();

        Taxi taxiOther = new Taxi(request.getId(), request.getIp(), request.getPort());
        taxiOther.setPosition(new Position(request.getPosition().getX(), request.getPosition().getY()));

        taxi.addTaxiToList(taxiOther);

        for (int i=0; i<taxi.getTaxiList().size(); i++) {
            System.out.println("Taxi: " + taxi.getTaxiList().get(i).getId() + " Position: " + taxi.getTaxiList().get(i).getPosition());
        }
    }

    @Override
    public void ride(Definition.RideRequest request, StreamObserver<Definition.RideResponse> responseStreamObserver) {

        boolean assign = true;

        for (Ride rideOther : taxi.getRideList()) {

            if (request.getRideId() == rideOther.getId()) {

                if (taxi.getInRide() == false) {

                    if (request.getDistance() == Position.getDistance(taxi.getPosition(), rideOther.getStartingPosition())) {

                        if (request.getTaxiBattery() == taxi.getBattery()) {

                            if (request.getTaxiId() < taxi.getId()) {

                                assign = false;

                            }

                        } else if (request.getTaxiBattery() > taxi.getBattery()) {
                            assign = true;
                        } else {
                            assign = false;
                        }

                    } else if (request.getDistance() < Position.getDistance(taxi.getPosition(), rideOther.getStartingPosition())) {
                        assign = true;
                    } else {
                        assign = false;
                    }

                } else {
                    assign = true;
                }

            }

        }

        Definition.RideResponse response = Definition.RideResponse
                .newBuilder()
                .setResponse(assign)
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();

    }

    @Override
    public void recharge(Definition.RechargeRequest request, StreamObserver<Definition.RechargeResponse> responseStreamObserver) {

        Definition.RechargeResponse response = Definition.RechargeResponse
                .newBuilder()
                .setFree(true)
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();

    }

}
