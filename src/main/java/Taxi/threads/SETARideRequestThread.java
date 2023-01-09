package taxi.threads;

import MQTT.Ride;
import com.google.gson.Gson;
import modules.Log;
import modules.Position;
import modules.Taxi;
import org.eclipse.paho.client.mqttv3.*;

import javax.lang.model.util.SimpleElementVisitor7;
import java.sql.Timestamp;

public class SETARideRequestThread extends Thread {

    private Taxi taxi;
    MqttClient client;
    String broker = "tcp://localhost:1883";
    String clientId = MqttClient.generateClientId();
    String subTopic = "seta/smartcity/rides/district"; // Topic per ricevere le Ride
    String pubTopic = "seta/smartcity/rides/done"; // Topic per mandare le Ride che faccio

    int qos = 2;

    boolean aBoolean = false; // Non mi piace!!!

    public SETARideRequestThread(Taxi taxi) { this.taxi = taxi; }

    @Override
    public void run() {

        //String district = Position.getDistrict(taxi.getPosition());

        //subTopic = subTopic + district;

        try {

            System.out.println("CLIENTID " + clientId);

            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connecting Broker " + broker);
            client.connect(connectOptions);
            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connected!");

            Gson gson = new Gson();

            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connection lost! cause: " + throwable.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    String receivedMessage = new String(message.getPayload());

                    Ride ride = gson.fromJson(receivedMessage, Ride.class);

                    System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] [RIDE: " + ride.getId() + "]" + Log.ANSI_RESET);

                    //taxi.addRideToList(ride); // Da togliere

                    taxi.startRideRequestThread(ride);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }

            });

            //System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribing ..." + Log.ANSI_RESET);
            //client.subscribe(subTopic,qos);
            //System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribed to topic : " + subTopic + Log.ANSI_RESET);

            subscribe();

            System.out.println("PROVAAAAAAA");

            while (!aBoolean) {

                if (taxi.getInRide() && taxi.getWichRide() != null) {

                    aBoolean = true;

                    System.out.println(Log.ANSI_RED + "Publishing!!!" + Log.ANSI_RESET);

                    // Avviso il MQTT Broker che sto facendo questa Ride

                    Ride ride = taxi.getWichRide();
                    String jsonRide = gson.toJson(ride);

                    MqttMessage sendMessage = new MqttMessage(jsonRide.getBytes());
                    sendMessage.setQos(qos);

                    System.out.println(Log.ANSI_RED + "[" + new Timestamp(System.currentTimeMillis()) + "] Publishing message: " + ride + " ..." + Log.ANSI_RESET);

                    //client.publish(topic + Position.getDistrict(ride.getStartingPosition()), message);
                    client.publish(pubTopic, sendMessage);

                    System.out.println(Log.ANSI_RED + "[" + new Timestamp(System.currentTimeMillis()) + "]Message published" + Log.ANSI_RESET);

                    /*

                    if (client.isConnected()) {
                        try {
                            System.out.println(Log.ANSI_PURPLE + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Unsubscribing ..." + Log.ANSI_RESET);
                            client.unsubscribe(pubTopic);
                            client.disconnectForcibly();
                            System.out.println(Log.ANSI_PURPLE + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Unsubscribed from topic : " + pubTopic + Log.ANSI_RESET);
                        } catch (MqttException mqttException) {
                            mqttException.printStackTrace();
                        }
                    }

                     */

                }
            }

        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        }

    }

    public void subscribe() {

        if (client.isConnected()) {

            try {

                String district = Position.getDistrict(taxi.getPosition());
                //String subTopic = SUB_TOPIC + district;

                System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribing ..." + Log.ANSI_RESET);
                client.subscribe(subTopic + district, qos);
                System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribed to topic : " + subTopic + district + Log.ANSI_RESET);

            } catch (MqttException mqttException) {
                mqttException.printStackTrace();
            }

        } else {
            System.out.println("NOT CONNECTED");
        }

    }

    public void unsubscribe(String topic) {

        if (client.isConnected()) {
            try {
                System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Unsubscribing ..." + Log.ANSI_RESET);
                client.unsubscribe(topic);
                //client.disconnectForcibly();
                System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Unsubscribed from topic : " + topic + Log.ANSI_RESET);
            } catch (MqttException mqttException) {
                mqttException.printStackTrace();
            }
        }

    }

}
