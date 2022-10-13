package modules;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.List;

@XmlRootElement
public class Report {

    private String time;
    private int taxi;
    private List<Double> pollution;
    private int rides;
    private double distance ;
    private int battery;

    public Report() {}

    public Report(int taxi, List<Double> pollution, int rides, double distance, int battery) {
        this.time = new Timestamp(System.currentTimeMillis()).toString();
        this.taxi = taxi;
        this.pollution = pollution;
        this.rides = rides;
        this.distance = distance;
        this.battery = battery;
    }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public int getTaxi() { return taxi;}

    public void setTaxi(int taxi) { this.taxi = taxi; }

    public List<Double> getPollution() { return pollution; }

    public void setPollution(List<Double> pollution) { this.pollution = pollution; }

    public int getRides() { return rides; }

    public void setRides(int rides) { this.rides = rides; }

    public double getDistance() { return distance; }

    public void setDistance(double distance) { this.distance = distance; }

    public int getBattery() { return battery; }

    public void setBattery(int battery) { this.battery = battery; }

    public String toString() {
        return "Time: " + getTime() + ", Taxi: " + getTaxi() + ", Pollution: " + getPollution() + ", Distance: " + getDistance() + ", Battery: " + getBattery() + ", Rides: " + getRides();
    }
}
