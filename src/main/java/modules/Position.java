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

}
