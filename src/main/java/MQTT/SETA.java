package MQTT;

import com.google.gson.Gson;
import modules.Position;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SETA {

    public static void main(String[] args) {

        MqttClient client;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String topic = "seta/smartcity/rides/district";
        int qos = 2;
        int rideId = 1;
        Gson gson = new Gson();

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println("Connecting broker ...");
            client.connect(connectOptions);
            System.out.println("Connected");

            while (true) {

                for (int i = 0; i < 2; i++) {

                    Ride ride = new Ride(rideId);
                    String jsonRide = gson.toJson(ride);

                    MqttMessage message = new MqttMessage(jsonRide.getBytes());
                    message.setQos(qos);

                    System.out.println("Publishing message: " + ride + " ...");

                    client.publish(topic + Position.getDistrict(ride.getStartingPosition()), message);

                    System.out.println("Message published");

                    rideId++;

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
