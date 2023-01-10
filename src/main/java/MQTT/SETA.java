package MQTT;

import com.google.gson.Gson;
import modules.Log;
import modules.Position;
import modules.Taxi;
import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class SETA {

    public static void main(String[] args) {

        MqttClient client;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String pubTopic = "seta/smartcity/rides/district";
        String subTopic = "seta/smartcity/rides/done";

        int qos = 2;

        int rideId = 1;

        Gson gson = new Gson();

        //ArrayList<Ride> rideList = new ArrayList<Ride>();

        Rides rides = new Rides();

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println(Log.ANSI_PURPLE + "[" + new Timestamp(System.currentTimeMillis()) + "] [BROKER MQTT] Connecting Broker " + broker + Log.ANSI_RESET);
            client.connect(connectOptions);
            System.out.println(Log.ANSI_PURPLE + "[" + new Timestamp(System.currentTimeMillis()) + "] [BROKER MQTT] Connected!" + Log.ANSI_RESET);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connection lost! cause: " + throwable.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    if (Objects.equals(topic, "seta/smartcity/rides/seta")) {

                        // NON SERVE PIÚ

                        /*

                        String receivedMessage = new String(message.getPayload());

                        Ride ride = gson.fromJson(receivedMessage, Ride.class);
                        rideList.add(ride);

                        System.out.println("[" + new Timestamp(System.currentTimeMillis())+ "] " + ride);

                        // Invio la richiesta ai Taxi

                        MqttMessage sendMessage = new MqttMessage(receivedMessage.getBytes());
                        message.setQos(qos);

                        client.publish(pubTopic + Position.getDistrict(ride.getStartingPosition()), sendMessage);

                        */

                    } else if (Objects.equals(topic, "seta/smartcity/rides/done")) {

                        String receivedMessage = new String(message.getPayload());
                        Ride ride = gson.fromJson(receivedMessage, Ride.class);

                        //System.out.println(Log.ANSI_RED + "[" + new Timestamp(System.currentTimeMillis())+ "] " + ride + Log.ANSI_RESET);

                        System.out.println(Log.ANSI_RED + "[" + new Timestamp(System.currentTimeMillis())+ "] [RIDE: " + ride.getId()  + "] [DISTRICT: " + Position.getDistrictFromPosition(ride.getStartingPosition()) + "]" + Log.ANSI_RESET);

                        // Remove della Ride

                        //rideList.removeIf(r -> r.getId() == ride.getId());
                        rides.remove(ride);


                    }


                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });

            System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribing ..." + Log.ANSI_RESET);
            client.subscribe(subTopic,qos);
            System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribed to topic : " + subTopic + Log.ANSI_RESET);

            while (true) {

                for (int i = 0; i < 2; i++) {

                    Ride ride = new Ride(rideId, new Position().getRandomStartingDestinationPosition(), new Position().getRandomStartingDestinationPosition());

                    //rideList.add(ride);
                    rides.add(ride);

                    rideId++;

                    System.out.println("[" + new Timestamp(System.currentTimeMillis())+ "] [RIDE: " + ride.getId()  + "] [DISTRICT: " + Position.getDistrictFromPosition(ride.getStartingPosition()) + "]");

                }

                for (int i = 0; i < rides.size(); i++) {
                    String jsonRide = gson.toJson(rides.get(i));
                    MqttMessage sendMessage = new MqttMessage(jsonRide.getBytes());
                    sendMessage.setQos(qos);
                    client.publish(pubTopic + Position.getDistrictFromPosition(rides.get(i).getStartingPosition()), sendMessage);
                }

                Thread.sleep(5000);

            }

        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
