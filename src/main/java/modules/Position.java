package modules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Random;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Position {

    private int x;
    private int y;

    public Position() {}

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() { return this.x + " " + this.y; }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Position getRandomPosition() {

        Random random = new Random();

        int x = random.nextInt(2) == 1 ? 0 : 9;
        int y = random.nextInt(2) == 1 ? 0 : 9;

        return new Position(x, y);

    }

    public Position getRandomStartingDestinationPosition() {

        Random random = new Random();

        int x = random.nextInt(10);
        int y = random.nextInt(10);

        return new Position(x, y);
    }

    public Position getPositionFromDistrict(String district) {

        if (district == "1") {
            return new Position(0,0);
        } else if (district == "2") {
            return new Position(9,0);
        } else if (district == "3") {
            return new Position(9,9);
        } else if (district == "4") {
            return new Position(0,9);
        } else {
            return null;
        }

    }

    public static String getDistrictFromPosition(Position position) {

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

    public static double getDistance(Position startingPosition, Position destinationPosition) {

        double distance;

        distance = Math.sqrt(
                Math.pow(startingPosition.getX() - destinationPosition.getX(), 2) +
                Math.pow(startingPosition.getY() - destinationPosition.getY(), 2)
        );

        return distance;

    }

}
