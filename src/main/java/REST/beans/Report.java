package REST.beans;

public class Report {

    private int id;
    private int pollution = 0;
    private int ride = 0;
    private int distance = 0;
    private int battery = 100;

    public Report() {}

    public int getId() { return id; }

    public int getPollution() { return pollution; }

    public int getRide() { return ride; }

    public int getDistance() { return distance; }

    public int getBattery() { return battery; }
}
