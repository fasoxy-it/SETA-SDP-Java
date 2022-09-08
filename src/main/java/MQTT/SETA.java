package MQTT;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.lang.Thread.sleep;

public class SETA {

    public static void main(String[] args) {

        MqttClient client;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String topic = "seta/smartcity/rides/district";
        int qos = 2;
        int rideId = 1;

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client.connect(connectOptions);

            System.out.println("Connected");

            while (true) {

                for (int i = 0; i < 2; i++) {

                    Ride ride = new Ride(rideId);
                    MqttMessage message = new MqttMessage(ride.toString().getBytes());
                    message.setQos(qos);

                    System.out.println("Publishing message: " + ride + " ...");

                    client.publish(topic + ride.getDistrict(ride.getStartingPosition()), message);

                    System.out.println("Message published");

                    rideId++;

                }

                sleep(5000);

            }

        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
