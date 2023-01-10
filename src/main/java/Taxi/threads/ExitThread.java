package taxi.threads;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import modules.Log;
import modules.Taxi;
import proto.Definition;
import proto.ManagerGrpc;

import java.sql.Timestamp;

public class ExitThread {

    Taxi taxi;
    Taxi otherTaxi;

    public ExitThread(Taxi taxi, Taxi otherTaxi) {
        this.taxi = taxi;
        this.otherTaxi = otherTaxi;
    }

    public void run() {

        final ManagedChannel channel = ManagedChannelBuilder.forTarget(otherTaxi.getIp() + ":" + String.valueOf(otherTaxi.getPort())).usePlaintext().build();

        ManagerGrpc.ManagerStub stub = ManagerGrpc.newStub(channel);

        Definition.ExitMessage request = Definition.ExitMessage
                .newBuilder()
                .setId(taxi.getId())
                .setIp(taxi.getIp())
                .setPort(taxi.getPort())
                .build();

        stub.exit(request, new StreamObserver<Definition.ExitResponse>() {
            @Override
            public void onNext(Definition.ExitResponse exitResponse) {
                //System.out.println(Log.ANSI_CYAN + "[" + new Timestamp(System.currentTimeMillis()) + "] " + exitResponse.getOk() + Log.ANSI_RESET);
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
