package REST.beans;

public class Report {

    private int id;
    private int taxi;
    private int pollution = 0;
    private int ride = 0;
    private int distance = 0;
    private int battery;

    public Report() {}

    public int getId() { return id; }

    public void setId() {
        this.id = Reports.getInstance().getLastReport() + 1;
    }

    public int getTaxi() { return taxi;}

    public int getPollution() { return pollution; }

    public int getRide() { return ride; }

    public int getDistance() { return distance; }

    public int getBattery() { return battery; }
}
