package MQTT;

import javafx.geometry.Pos;
import modules.Position;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.omg.CORBA.PRIVATE_MEMBER;
import proto.Definition;

import java.sql.Timestamp;

public class Ride {

    private String time;
    private int id;
    private Position startingPosition;
    private Position destinationPosition;

    @JsonIgnore
    private int countRequest = 0;
    @JsonIgnore
    private int countResponse = 0;

    public Ride() {}

    public Ride(int id, Position startingPosition, Position destinationPosition) {
        this.time = new Timestamp(System.currentTimeMillis()).toString();
        this.id = id;
        this.startingPosition = startingPosition; //new Position().getRandomStartingDestinationPosition();
        this.destinationPosition = destinationPosition; //new Position().getRandomStartingDestinationPosition();
    }

    public String toString() { return "Time: " + this.time + ", Id: " + this.id + ", Starting position: (" + this.startingPosition.getX() + ", " + this.startingPosition.getY() + "), Destination position: (" + this.destinationPosition.getX() + ", " + this.destinationPosition.getY() + ")"; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public int getId() { return id; }

    public Position getStartingPosition() {
        return startingPosition;
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }

    public synchronized int getCountRequest() {
        return countRequest;
    }

    public synchronized int getCountResponse() {
        return countResponse;
    }

    public synchronized void addCountRequest() {
        countRequest++;
    }

    public synchronized void addCountResponse() {
        countResponse++;
    }

}
