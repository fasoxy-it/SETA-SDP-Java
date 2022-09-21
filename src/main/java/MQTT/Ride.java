package MQTT;

import modules.Position;

public class Ride {

    private int id;
    private Position startingPosition;
    private Position destinationPosition;

    public Ride(int id) {
        this.id = id;
        this.startingPosition = new Position().getRandomSomething();
        this.destinationPosition = new Position().getRandomSomething();
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
