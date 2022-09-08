package MQTT;

import java.util.Random;

public class Ride {

    private int id;
    private int[] startingPosition;
    private int[] destinationPosition;

    public Ride(int id) {
        this.id = id;
        this.startingPosition = RandomPosition();
        this.destinationPosition = RandomPosition();
    }

    public String toString() { return "Id: " + this.id + ", Starting position: (" + this.startingPosition[0] + ", " + this.startingPosition[1] + "), Destination position: (" + this.destinationPosition[0] + ", " + this.destinationPosition[1] + ")"; }

    public int getId() { return id; }

    public int[] getStartingPosition() {
        return startingPosition;
    }

    public int[] getDestinationPosition() {
        return destinationPosition;
    }

    private int[] RandomPosition() {

        Random random = new Random();

        int x = random.nextInt(10);
        int y = random.nextInt(10);

        return new int[]{x, y};

    }

    public String getDistrict(int[] position) {

        int x = position[0];
        int y = position[1];

        if (x < 5 && y < 5) {
            return "1";
        } else if (x > 4 && y < 5) {
            return "2";
        } else if (x > 4 && y > 4) {
            return "3";
        } else if (x < 5 && y > 4) {
            return "4";
        } else {
            return null;
        }

    }
}
