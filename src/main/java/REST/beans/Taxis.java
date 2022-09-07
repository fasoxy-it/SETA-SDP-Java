package REST.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Taxis {

    @XmlElement(name = "taxis")
    private List<Taxi> taxiList;

    private static Taxis instance;

    private Taxis() { taxiList = new ArrayList<Taxi>(); }

    public synchronized static Taxis getInstance() {

        if (instance == null) {
            instance = new Taxis();
        }

        return instance;
    }

    public synchronized List<Taxi> getTaxiList() {
        return new ArrayList<>(taxiList);
    }

    public synchronized Taxi add(Taxi taxi) {

        for (Taxi oldTaxi: getTaxiList()) {
            if (oldTaxi.getId() == taxi.getId()) {
                return null;
            }
        }

        Integer[] position = RandomPosition();
        taxi.setPosition(position);

        taxiList.add(taxi);
        return taxi;

    }

    public synchronized void remove(int taxiId) {

        for (Taxi taxi: getTaxiList()) {
            if (taxi.getId() == taxiId) {
                taxiList.remove(taxi);
            }
        }

    }

    private Integer[] RandomPosition() {

        Random random = new Random();

        int x = random.nextInt(2) == 1 ? 0 : 9;
        int y = random.nextInt(2) == 1 ? 0 : 9;

        return new Integer[]{x, y};

    }

}
