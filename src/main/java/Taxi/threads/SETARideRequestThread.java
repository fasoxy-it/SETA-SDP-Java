package taxi.threads;

import MQTT.Ride;
import com.google.gson.Gson;
import modules.Log;
import modules.Position;
import modules.Taxi;
import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;

public class SETARideRequestThread extends Thread {

    private Taxi taxi;

    MqttClient client;
    String broker = "tcp://localhost:1883";
    String clientId = MqttClient.generateClientId();
    String topic = "seta/smartcity/rides/district";

    int qos = 2;

    public SETARideRequestThread(Taxi taxi) { this.taxi = taxi; }

    @Override
    public void run() {

        String district = Position.getDistrict(taxi.getPosition());

        topic = topic + district;

        try {

            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connecting Broker " + broker);
            client.connect(connectOptions);
            System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connected!");

            Gson gson = new Gson();

            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Connection lost! cause: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());
                    /*
                    System.out.println("[SETA] Received a Ride!" +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + receivedMessage);
                    */
                    Ride ride = gson.fromJson(receivedMessage, Ride.class);
                    System.out.println(Log.ANSI_YELLOW + "[" + time + "] [SETA] [RIDE: " + ride.getId() + "]" + Log.ANSI_RESET);

                    taxi.addRideToList(ride);

                    System.out.println(Log.ANSI_PURPLE + taxi.getRideList() + Log.ANSI_RESET);

                    taxi.startRideThread(ride);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }

            });

            System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribing ..." + Log.ANSI_RESET);
            client.subscribe(topic,qos);
            System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Subscribed to topic : " + topic + Log.ANSI_RESET);

        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        }

    }

    public void unsubscribe() {

        if (client.isConnected()) {
            try {
                System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Unsubscribing ..." + Log.ANSI_RESET);
                //client.unsubscribe(topic);
                client.disconnectForcibly();
                System.out.println(Log.ANSI_YELLOW + "[" + new Timestamp(System.currentTimeMillis()) + "] [SETA] Unsubscribed from topic : " + topic + Log.ANSI_RESET);
            } catch (MqttException mqttException) {
                mqttException.printStackTrace();
            }
        }


    }

}
