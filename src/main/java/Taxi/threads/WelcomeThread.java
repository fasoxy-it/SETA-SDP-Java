package taxi.threads;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import modules.Position;
import modules.Taxi;
import proto.Definition;
import proto.ManagerGrpc;

public class WelcomeThread extends Thread {

    Taxi taxi;
    Taxi otherTaxi;

    public WelcomeThread(Taxi taxi, Taxi otherTaxi) {
        this.taxi = taxi;
        this.otherTaxi = otherTaxi;
    }

    public void run() {

        final ManagedChannel channel = ManagedChannelBuilder.forTarget(otherTaxi.getIp() + ":" + String.valueOf(otherTaxi.getPort())).usePlaintext().build();

        ManagerGrpc.ManagerStub stub = ManagerGrpc.newStub(channel);

        Definition.WelcomeMessage request = Definition.WelcomeMessage
                .newBuilder()
                .setId(taxi.getId())
                .setIp(taxi.getIp())
                .setPort(taxi.getPort())
                .setPosition(Definition.Position
                        .newBuilder()
                        .setX(taxi.getPosition().getX())
                        .setY(taxi.getPosition().getY())
                        .build()
                )
                .build();

        stub.welcome(request, new StreamObserver<Definition.WelcomeResponse>() {

            @Override
            public void onNext(Definition.WelcomeResponse welcomeResponse) {

                // Risposta dagli altri taxi già presenti
                System.out.println("Taxi: " + welcomeResponse.getId() + " Position: " + welcomeResponse.getPosition().getX() + " " + welcomeResponse.getPosition().getY());

                otherTaxi.setPosition(new Position(welcomeResponse.getPosition().getX(), welcomeResponse.getPosition().getY()));

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
