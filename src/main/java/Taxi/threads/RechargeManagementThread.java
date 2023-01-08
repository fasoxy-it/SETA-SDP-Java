package taxi.threads;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import modules.Log;
import modules.Position;
import modules.Taxi;
import proto.Definition;
import proto.ManagerGrpc;

import java.sql.Timestamp;

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
                .build();

        System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [CHARGE] [SENDER]: Request of charging to: " + otherTaxi.getId());

        stub.recharge(request, new StreamObserver<Definition.RechargeResponse>() {
            @Override
            public void onNext(Definition.RechargeResponse rechargeResponse) {
                System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [CHARGE] [RECIVER]: Response of charging from: " + otherTaxi.getId() + " with value of: " + rechargeResponse.getFree());
                if (rechargeResponse.getFree()) {
                    rechargeLock.wakeUp();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                channel.shutdownNow();
                if (throwable.getMessage().equals("UNAVAILABLE: io exception")) {
                    System.err.println("[" + new Timestamp(System.currentTimeMillis()) + "] [CHARGE] " + otherTaxi.getId() + " not responding!");
                    taxi.removeTaxiFromList(otherTaxi.getId());
                    taxi.startRechargeRequestThread();
                }
            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }
        });

    }

}
