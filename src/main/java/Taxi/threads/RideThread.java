package taxi.threads;

import MQTT.Ride;
import com.google.gson.Gson;
import modules.Position;
import modules.Taxi;
import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;
import java.util.Scanner;

public class RideThread extends Thread {

    private Taxi taxi;

    public RideThread(Taxi taxi) { this.taxi = taxi; }

    @Override
    public void run() {

        MqttClient client;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String topic = "seta/smartcity/rides/district";
        String district = Position.getDistrict(taxi.getPosition());

        topic = topic + district;

        int qos = 2;

        try {

            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println("Connecting Broker " + broker);
            client.connect(connectOptions);
            System.out.println("Connected");

            Gson gson = new Gson();

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost! cause:" + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());
                    System.out.println("Received a Message!" +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + receivedMessage +
                            "\n\tQoS:     " + message.getQos() + "\n");

                    System.out.println("\n ***  Press a random key to exit *** \n");

                    Ride ride = gson.fromJson(receivedMessage, Ride.class);

                    for (Taxi otherTaxi : taxi.getTaxiList()) {

                        RideRequestThread rideRequestThread = new RideRequestThread(taxi, otherTaxi, ride);
                        rideRequestThread.run();

                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            System.out.println("Subscribing ...");
            client.subscribe(topic,qos);
            System.out.println("Subscribed to topics : " + topic);

            System.out.println("\n ***  Press a random key to exit *** \n");
            Scanner command = new Scanner(System.in);
            command.nextLine();
            client.disconnect();

        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        }

    }

}
