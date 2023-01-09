package MQTT;

import com.google.gson.Gson;
import com.sun.xml.bind.v2.TODO;
import modules.Log;
import modules.Position;
import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

public class MQTTBroker {

    public static void main(String[] args) {

        MqttClient client;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String pubTopic = "seta/smartcity/rides/district";
        String subTopics [] = new String[] {"seta/smartcity/rides/seta", "seta/smartcity/rides/done"};
        String subTopic1 = "seta/smartcity/rides/seta";
        String subTopic2 = "seta/smartcity/rides/done";
        int subQos [] = new int[] {2,2};

        int qos = 2;

        Gson gson = new Gson();

        ArrayList<Ride> rideList = new ArrayList<Ride>();

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connecting Broker " + broker);
            client.connect(connectOptions);
            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connected!");

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connection lost! cause: " + throwable.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    System.out.println(topic);

                    if (Objects.equals(topic, "seta/smartcity/rides/seta")) {

                        String receivedMessage = new String(message.getPayload());

                        Ride ride = gson.fromJson(receivedMessage, Ride.class);
                        rideList.add(ride);

                        System.out.println(rideList);

                        // Invio la richiesta ai Taxi

                        MqttMessage sendMessage = new MqttMessage(receivedMessage.getBytes());
                        message.setQos(qos);

                        System.out.println("Publishing message: " + ride + "...");
                        client.publish(pubTopic + Position.getDistrict(ride.getStartingPosition()), sendMessage);
                        System.out.println("Message published");

                    } else if (Objects.equals(topic, "seta/smartcity/rides/done")) {

                        String receivedMessage = new String(message.getPayload());
                        Ride ride = gson.fromJson(receivedMessage, Ride.class);

                        System.out.println(Log.ANSI_RED + ride + Log.ANSI_RESET);

                        // Remove della Ride

                        rideList.removeIf(r -> r.getId() == ride.getId());

                        System.out.println(rideList);

                    }


                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });

            System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribing ..." + Log.ANSI_RESET);
            client.subscribe(subTopic1,qos);
            System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribed to topic : " + subTopic1 + Log.ANSI_RESET);

            System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribing ..." + Log.ANSI_RESET);
            client.subscribe(subTopic2,qos);
            System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribed to topic : " + subTopic2 + Log.ANSI_RESET);



        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        }

    }

}
