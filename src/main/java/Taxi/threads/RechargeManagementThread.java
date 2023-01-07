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

        boolean[] nextNotResponding = {false};

        ManagerGrpc.ManagerStub stub = ManagerGrpc.newStub(channel);

        Definition.RechargeRequest request = Definition.RechargeRequest
                .newBuilder()
                .setTaxiId(taxi.getId())
                .setDistrict(Integer.parseInt(Position.getDistrict(taxi.getPosition())))
                .setTimestamp(taxi.getWantCharge())
                .build();

        System.out.println("[CHARGE] [SENDER]: Request of charging from: " + taxi.getId() + " to: " + otherTaxi.getId());

        stub.recharge(request, new StreamObserver<Definition.RechargeResponse>() {
            @Override
            public void onNext(Definition.RechargeResponse rechargeResponse) {
                System.out.println("[CHARGE] [RECIVER]: Response of charging from: " + otherTaxi.getId() + " with value of: " + rechargeResponse.getFree() + " at timestamp: " + rechargeResponse.getTimestamp());
                if (rechargeResponse.getFree()) {
                    rechargeLock.wakeUp();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                channel.shutdownNow();
                if (throwable.getMessage().equals("UNAVAILABLE: io exception")) {
                    System.err.println(otherTaxi.getId() + " not responding!");
                    nextNotResponding[0] = true;
                    synchronized (nextNotResponding) {
                        System.out.println("PROVA NOTIFY");
                        nextNotResponding.notify();
                    }
                }
            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }
        });

        try {
            synchronized (nextNotResponding) {
                System.out.println("PROVA WAIT");
                nextNotResponding.wait();
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        if (nextNotResponding[0]) {
            System.out.println("RERUN startRechargeRequestThread");
            taxi.removeTaxiFromList(otherTaxi.getId());
            System.out.println(taxi.getTaxiList());
            taxi.startRechargeRequestThread();
        }

    }

}
