package taxi.threads;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import modules.Position;
import modules.Taxi;
import proto.Definition;
import proto.ManagerGrpc;

public class RechargeManagementThread extends Thread {

    Taxi taxi;
    Taxi otherTaxi;

    RechargeLock rechargeLock;

    public RechargeManagementThread(Taxi taxi, Taxi otherTaxi, RechargeLock rechargeLock) {
        this.taxi = taxi;
        this.otherTaxi = otherTaxi;
        this.rechargeLock = rechargeLock;
    }

    public void run() {

        final ManagedChannel channel = ManagedChannelBuilder.forTarget(otherTaxi.getIp() + ":" + otherTaxi.getPort()).usePlaintext().build();

        ManagerGrpc.ManagerStub stub = ManagerGrpc.newStub(channel);

        Definition.RechargeRequest request = Definition.RechargeRequest
                .newBuilder()
                .setDistrict(Integer.parseInt(Position.getDistrict(taxi.getPosition())))
                .setId(taxi.getId())
                .build();

        stub.recharge(request, new StreamObserver<Definition.RechargeResponse>() {
            @Override
            public void onNext(Definition.RechargeResponse rechargeResponse) {
                if (rechargeResponse.getFree()) {
                    rechargeLock.wakeUp();
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }
        });

    }

}
