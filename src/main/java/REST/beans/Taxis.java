package REST.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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

    public synchronized void add(Taxi taxi) { taxiList.add(taxi); }

    public synchronized void remove(int taxiId) {

        for (Taxi taxi: getTaxiList()) {
            if (taxi.getId() == taxiId) {
                taxiList.remove(taxi);
            }
        }

    }

}
