package GRPC;

import MQTT.Ride;
import io.grpc.stub.StreamObserver;
import modules.Log;
import modules.Position;
import modules.Taxi;
import proto.Definition;
import proto.ManagerGrpc;

import java.sql.Timestamp;
import java.time.Instant;

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
            System.out.println(Log.ANSI_CYAN + "[" + new Timestamp(System.currentTimeMillis()) + "] New Taxi joined the Smart City with Id: " + taxi.getTaxiList().get(i).getId() + " and Position: (" + taxi.getTaxiList().get(i).getPosition() + ")" + Log.ANSI_RESET);
            //System.out.println("Taxi: " + taxi.getTaxiList().get(i).getId() + " Position: " + taxi.getTaxiList().get(i).getPosition());
        }
    }

    @Override
    public void ride(Definition.RideRequest request, StreamObserver<Definition.RideResponse> responseStreamObserver) {

        boolean assign = true;

        Ride ride = new Ride(request.getRide().getId(), new Position(request.getRide().getStartingPosition().getX(), request.getRide().getStartingPosition().getY()), new Position(request.getRide().getDestinationPosition().getX(), request.getRide().getDestinationPosition().getY()));

        if (!taxi.getInRide() && !taxi.getInCharge() && taxi.getWantCharge() != null) {

            if (Integer.parseInt(Position.getDistrictFromPosition(taxi.getPosition())) == Integer.parseInt(Position.getDistrictFromPosition(ride.getStartingPosition()))) {

                //taxi.addRideToList(ride); // Bisogna controllare che non ci sia già

                if (request.getDistance() == Position.getDistance(taxi.getPosition(), ride.getStartingPosition())) {

                    if (request.getTaxiBattery() == taxi.getBattery()) {

                        if (request.getTaxiId() < taxi.getId()) {

                            assign = false;

                        }

                    } else if (request.getTaxiBattery() > taxi.getBattery()) {

                        assign = true;

                    } else {

                        assign = false;

                    }

                } else if (request.getDistance() < Position.getDistance(taxi.getPosition(), ride.getStartingPosition())) {

                    assign = true;

                } else {

                    assign = false;

                }

            }

        } else if (taxi.getInRide()) {

            if (taxi.getWichRide().getId() == request.getRideId()) {

                assign = false;

                System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] Sto facendo questa!"); // Da togliere

            } else {

                assign = true;

            }


        } else {

            assign = true;

        }

        /*
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
        */

        Definition.RideResponse response = Definition.RideResponse
                .newBuilder()
                .setResponse(assign)
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();

    }
    @Override
    public void recharge(Definition.RechargeRequest request, StreamObserver<Definition.RechargeResponse> responseStreamObserver) {

        //System.out.println("RECIVER Request of charging from: " + request.getTaxiId() + " to: " + taxi.getId());

        if (Integer.parseInt(Position.getDistrictFromPosition(taxi.getPosition())) == request.getDistrict()) {
            if (!taxi.getInCharge() && taxi.getWantCharge() == null || taxi.getId() == request.getTaxiId()) {
                //System.out.println("0");
                Definition.RechargeResponse response = Definition.RechargeResponse
                        .newBuilder()
                        .setTaxiId(taxi.getId())
                        .setFree(true)
                        .setTimestamp( String.valueOf(new Timestamp(System.currentTimeMillis())))
                        .build();

                responseStreamObserver.onNext(response);
                responseStreamObserver.onCompleted();
            } else if (taxi.getInCharge()) {
                //System.out.println("1");
                taxi.rechargeLockServer.block();
                Definition.RechargeResponse response = Definition.RechargeResponse
                        .newBuilder()
                        .setTaxiId(taxi.getId())
                        .setFree(true)
                        .setTimestamp( String.valueOf(new Timestamp(System.currentTimeMillis())))
                        .build();

                responseStreamObserver.onNext(response);
                responseStreamObserver.onCompleted();
            } else if (!taxi.getInCharge() && taxi.getWantCharge() != null) {

                Instant requestInstant = Instant.ofEpochSecond( new Timestamp(Long.valueOf(request.getTimestamp())).getTime());
                System.out.println("Taxi: " + request.getTaxiId() + " Request Instant: " + new Timestamp(Long.valueOf(request.getTimestamp())));
                Instant responseInstant = Instant.ofEpochSecond( new Timestamp(Long.valueOf(taxi.getWantCharge())).getTime());
                System.out.println("Taxi: " + taxi.getId() + " Response Instant: " + new Timestamp(Long.valueOf(taxi.getWantCharge())));
                if (requestInstant.isBefore(responseInstant)) {
                    //System.out.println("2");
                    Definition.RechargeResponse response = Definition.RechargeResponse
                            .newBuilder()
                            .setTaxiId(taxi.getId())
                            .setFree(true)
                            .setTimestamp( String.valueOf(new Timestamp(System.currentTimeMillis())))
                            .build();

                    responseStreamObserver.onNext(response);
                    responseStreamObserver.onCompleted();
                } else if (responseInstant.isBefore(requestInstant)) {
                    //System.out.println("3");
                    taxi.rechargeLockServer.block();
                    Definition.RechargeResponse response = Definition.RechargeResponse
                            .newBuilder()
                            .setTaxiId(taxi.getId())
                            .setFree(true)
                            .setTimestamp( String.valueOf(new Timestamp(System.currentTimeMillis())))
                            .build();

                    responseStreamObserver.onNext(response);
                    responseStreamObserver.onCompleted();
                } else {
                    if (request.getTaxiId() < taxi.getId()) {
                        taxi.rechargeLockServer.block();
                        Definition.RechargeResponse response = Definition.RechargeResponse
                                .newBuilder()
                                .setTaxiId(taxi.getId())
                                .setFree(true)
                                .setTimestamp( String.valueOf(new Timestamp(System.currentTimeMillis())))
                                .build();

                        responseStreamObserver.onNext(response);
                        responseStreamObserver.onCompleted();
                    } else {
                        Definition.RechargeResponse response = Definition.RechargeResponse
                                .newBuilder()
                                .setTaxiId(taxi.getId())
                                .setFree(true)
                                .setTimestamp( String.valueOf(new Timestamp(System.currentTimeMillis())))
                                .build();

                        responseStreamObserver.onNext(response);
                        responseStreamObserver.onCompleted();
                    }
                }
            }
        } else {
            Definition.RechargeResponse response = Definition.RechargeResponse
                    .newBuilder()
                    .setTaxiId(taxi.getId())
                    .setFree(true)
                    .setTimestamp( String.valueOf(new Timestamp(System.currentTimeMillis())))
                    .build();

            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
        }

    }

}
