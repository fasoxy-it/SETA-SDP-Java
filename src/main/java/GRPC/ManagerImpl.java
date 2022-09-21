package GRPC;

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
                .setMaster(taxi.isMaster())
                .setPosition(Definition.Position
                        .newBuilder()
                        .setX(taxi.getPosition().getX())
                        .setY(taxi.getPosition().getY())
                        .build()
                )
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();

        System.out.println("Numero di taxi: " + taxi.getTaxiList().size());
        System.out.println("Taxi: " + request.getId() + " Position: (x: " + request.getPosition().getX() + ", y: " + request.getPosition().getY() + ")");

        Taxi taxiOther = new Taxi(request.getId(), request.getIp(), request.getPort());
        taxiOther.setPosition(new Position(request.getPosition().getX(), request.getPosition().getY()));

        taxi.addTaxiToList(taxiOther);

        System.out.println("Numero di taxi: " + taxi.getTaxiList().size());
        System.out.println("Taxi: " + taxi.getTaxiList());

        for (int i=0; i<taxi.getTaxiList().size(); i++) {
            System.out.println("Taxi: " + taxi.getTaxiList().get(i).getId() + " Position: " + taxi.getTaxiList().get(i).getPosition());
        }
    }

}
