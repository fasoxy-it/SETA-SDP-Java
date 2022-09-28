package MQTT;

import modules.Position;
import proto.Definition;

public class Ride {

    private int id;
    private Position startingPosition;
    private Position destinationPosition;

    public Ride() {}

    public Ride(int id) {
        this.id = id;
        this.startingPosition = new Position().getRandomStartingDestinationPosition();
        this.destinationPosition = new Position().getRandomStartingDestinationPosition();
    }

    public String toString() { return "Id: " + this.id + ", Starting position: (" + this.startingPosition.getX() + ", " + this.startingPosition.getY() + "), Destination position: (" + this.destinationPosition.getX() + ", " + this.destinationPosition.getY() + ")"; }

    public int getId() { return id; }

    public Position getStartingPosition() {
        return startingPosition;
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }

}
