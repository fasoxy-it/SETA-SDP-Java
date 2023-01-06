package taxi.threads;

import com.google.protobuf.Timestamp;
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
                .setTaxiId(taxi.getId())
                .setDistrict(Integer.parseInt(Position.getDistrict(taxi.getPosition())))
                .setTimestamp(taxi.getWantCharge())

                // Penso vada messo il timestamp del momento in cui il Taxi ha necessita di ricaricarsi


                .build();

        System.out.println("SENDER Request of charging from: " + request.getTaxiId() + " to: " + otherTaxi.getId());

        stub.recharge(request, new StreamObserver<Definition.RechargeResponse>() {
            @Override
            public void onNext(Definition.RechargeResponse rechargeResponse) {
                System.out.println("SENDER R Request of charging from: " + rechargeResponse.getTaxiId() + " with value of: " + rechargeResponse.getFree() + " at timestamp: " + rechargeResponse.getTimestamp());
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
