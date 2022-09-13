package modules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AddTaxiResponse {

    private List<Taxi> taxis;
    private Position position;

    public AddTaxiResponse() {}

    public AddTaxiResponse(List<Taxi> taxis, Position position) {
        this.taxis = taxis;
        this.position = position;
    }

    public List<Taxi> getTaxis() { return taxis; }

    public void setTaxis(List<Taxi> taxis) { this.taxis = taxis; }

    public Position getPosition() { return position; }

    public void setPosition(Position position) { this.position = position; }

}
