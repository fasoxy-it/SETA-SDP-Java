package taxi.threads;

import MQTT.Ride;
import com.google.gson.Gson;
import modules.Position;
import modules.Taxi;
import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

public class RideRequestThread extends Thread {

    private Taxi taxi;

    MqttClient client;
    String broker = "tcp://localhost:1883";
    String clientId = MqttClient.generateClientId();
    String topic = "seta/smartcity/rides/district";

    int qos = 2;

    public RideRequestThread(Taxi taxi) { this.taxi = taxi; }

    @Override
    public void run() {

        String district = Position.getDistrict(taxi.getPosition());

        topic = topic + district;

        try {

            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println("Connecting Broker " + broker);
            client.connect(connectOptions);
            System.out.println("Connected!");

            Gson gson = new Gson();

            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost! cause: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());
                    System.out.println("Received a Ride!" +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + receivedMessage);

                    Ride ride = gson.fromJson(receivedMessage, Ride.class);
                    taxi.addRideToList(ride);

                    RideLock rideLock = new RideLock(taxi, ride);

                    for (Taxi otherTaxi : taxi.getTaxiList()) {

                        //if (taxi.getId() != otherTaxi.getId()) {

                            //taxi.getRide(ride.getId()).addCountRequest();

                            RideManagementThread rideManagementThread = new RideManagementThread(taxi, otherTaxi, ride, rideLock);
                            rideManagementThread.start();
                            //rideManagementThread.join();

                        //}

                    }

                    rideLock.block();

                    /*
                    if (taxi.getRide(ride.getId()).getCountRequest() == taxi.getRide(ride.getId()).getCountResponse()) {

                        RideThread rideThread = new RideThread(taxi, ride);
                        rideThread.start();

                    }
                    */

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }

            });

            System.out.println("Subscribing ...");
            client.subscribe(topic,qos);
            System.out.println("Subscribed to topic : " + topic);

        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        }

    }

    public void unsubscribe() {

        try {
            System.out.println("Unsubscribing ...");
            client.unsubscribe(topic);
            System.out.println("Unsubscribed from topic : " + topic);
        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        }

    }
}

class RideLock {

    public int responses;
    public int responsesTrue;
    Object lock;
    Taxi taxi;
    Ride ride;

    public RideLock(Taxi taxi, Ride ride) {
        responses = 0;
        responsesTrue = 0;
        lock = new Object();
        this.taxi = taxi;
        this.ride = ride;
    }

    public void block() {
        synchronized (lock) {
            System.out.println("Waiting...");
            while (responses < taxi.getTaxiList().size()) {
                try {
                    lock.wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        if (responses == responsesTrue) {

            System.out.println("Responses is equal to ResponsesTrue");

            if (!taxi.getInRide()) {
                RideThread rideThread = new RideThread(taxi, ride);
                rideThread.start();
            } else {
                System.err.println("[RIDE: " + ride.getId() + "] Can't do this ride because I'm already involved in another ride!");
                // Occorre rilanciare la ride!!!
            }
        }


    }

    public void wakeUp(boolean assign) {
        if (assign) {
            responsesTrue++;
        }

        responses++;

        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
