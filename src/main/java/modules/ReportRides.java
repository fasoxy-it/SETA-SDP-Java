package modules;

import java.util.List;

public class ReportRides {

    private double pollutionAvg;
    private int ridesAvg;
    private double distanceAvg;
    private int batteryAvg;

    public ReportRides() {}

    public ReportRides(double pollutionAvg, int ridesAvg, double distanceAvg, int batteryAvg) {
        this.pollutionAvg = pollutionAvg;
        this.ridesAvg = ridesAvg;
        this.distanceAvg = distanceAvg;
        this.batteryAvg = batteryAvg;
    }

    public double getPollutionAvg() { return pollutionAvg; }

    public void setPollutionAvg(double pollutionAvg) { this.pollutionAvg = pollutionAvg; }

    public int getRidesAvg() { return ridesAvg; }

    public void setRidesAvg(int ridesAvg) { this.ridesAvg = ridesAvg; }

    public double getDistanceAvg() { return distanceAvg; }

    public void setDistanceAvg(double distanceAvg) { this.distanceAvg = distanceAvg; }

    public int getBatteryAvg() { return batteryAvg; }

    public void setBatteryAvg(int batteryAvg) { this.batteryAvg = batteryAvg; }

    public String toString() {
        return "Pollution: " + getPollutionAvg() + ", Distance: " + getDistanceAvg() + ", Battery: " + getBatteryAvg() + ", Rides: " + getRidesAvg();
    }

}
