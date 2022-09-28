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

        boolean r = true;
        double d = 0.0;

        for (Ride rideOther : taxi.getRideList()) {

            if (request.getId() == rideOther.getId()) {

                d = Position.getDistance(taxi.getPosition(), rideOther.getStartingPosition());

                if (d < request.getDistance()) {
                    r = false;
                }

            }

        }

        Definition.RideResponse response = Definition.RideResponse
                .newBuilder()
                .setResponse(r)
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();

    }

}
