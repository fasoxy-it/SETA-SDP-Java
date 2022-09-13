package MQTT;

import modules.Position;

import java.util.Random;

public class Ride {

    private int id;
    private Position startingPosition;
    private Position destinationPosition;

    public Ride(int id) {
        this.id = id;
        this.startingPosition = new Position().getRandomPosition();
        this.destinationPosition = new Position().getRandomPosition();
    }

    public String toString() { return "Id: " + this.id + ", Starting position: (" + this.startingPosition.getX() + ", " + this.startingPosition.getY() + "), Destination position: (" + this.destinationPosition.getX() + ", " + this.destinationPosition.getY() + ")"; }

    public int getId() { return id; }

    public Position getStartingPosition() {
        return startingPosition;
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }

    public String getDistrict(Position position) {

        int x = position.getX();
        int y = position.getY();

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
