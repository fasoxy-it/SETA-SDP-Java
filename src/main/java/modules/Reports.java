package modules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Reports {

    @XmlElement(name = "reports")
    private List<Report> reportList;

    private static Reports instance;

    private Reports() { reportList = new ArrayList<Report>(); }

    public synchronized static Reports getInstance() {

        if (instance == null) {
            instance = new Reports();
        }

        return instance;
    }

    public synchronized List<Report> getReportList() { return reportList; }

    public synchronized ReportRides getLastNReportsFromReportListForTaxi(int n, int t) {

        List<Report> reportSubList = new ArrayList<Report>();

        for (Report report : reportList) {
            if (report.getTaxi() == t) {
                reportSubList.add(report);
            }
        }

        if (reportSubList.size() >= n + 1) {

            reportSubList = reportSubList.subList(reportSubList.size() - n, reportSubList.size());

        }

        List<Double> distanceList = new ArrayList<Double>();
        List<Integer> batteryList = new ArrayList<Integer>();
        List<Double> pollutionList = new ArrayList<Double>();
        List<Integer> ridesList = new ArrayList<Integer>();

        for (Report report : reportSubList) {

            distanceList.add(report.getDistance());
            batteryList.add(report.getBattery());

            double pollutionS = 0.0;

            for (double pollution : report.getPollution()) {
                pollutionS += pollution;
            }

            double pollutionA = pollutionS / report.getPollution().size();

            pollutionList.add(pollutionA);
            ridesList.add(report.getRides());

        }

        double distanceSum = 0.0;
        int batterySum = 0;
        double pollutionSum = 0.0;
        double ridesSum = 0.0;

        for (double distance : distanceList) {
            distanceSum += distance;
        }

        for (int battery : batteryList) {
            batterySum += battery;
        }

        for (double pollution : pollutionList) {
            pollutionSum += pollution;
        }

        for (double rides : ridesList) {
            ridesSum += rides;
        }

        double distanceAvg = distanceSum / distanceList.size();
        int batteryAvg = batterySum / batteryList.size();
        double pollutionAvg = pollutionSum / pollutionList.size();
        double ridesAvg = ridesSum / ridesList.size();

        ReportRides reportRides = new ReportRides(pollutionAvg, ridesAvg, distanceAvg, batteryAvg);

        return reportRides;

    }

    public synchronized ReportRides getReportsFromReportListBetweenTimestamps(String t1D, String t1T, String t2D, String t2T) {

        List<Report> reportSubList = new ArrayList<Report>();

        Timestamp timestampT1 = Timestamp.valueOf(t1D + " " +t1T);
        Timestamp timestampT2 = Timestamp.valueOf(t2D + " " +t2T);

        for (Report report : reportList) {

            Timestamp timestamp = Timestamp.valueOf(report.getTime());

            if (timestamp.after(timestampT1) && timestamp.before(timestampT2)) {
                reportSubList.add(report);
            }

        }

        List<Double> distanceList = new ArrayList<Double>();
        List<Integer> batteryList = new ArrayList<Integer>();
        List<Double> pollutionList = new ArrayList<Double>();
        List<Integer> ridesList = new ArrayList<Integer>();

        for (Report report : reportSubList) {

            distanceList.add(report.getDistance());
            batteryList.add(report.getBattery());

            double pollutionS = 0.0;

            for (double pollution : report.getPollution()) {
                pollutionS += pollution;
            }

            double pollutionA = pollutionS / report.getPollution().size();

            pollutionList.add(pollutionA);
            ridesList.add(report.getRides());

        }

        double distanceSum = 0.0;
        int batterySum = 0;
        double pollutionSum = 0.0;
        double ridesSum = 0.0;

        for (double distance : distanceList) {
            distanceSum += distance;
        }

        for (int battery : batteryList) {
            batterySum += battery;
        }

        for (double pollution : pollutionList) {
            pollutionSum += pollution;
        }

        for (double rides : ridesList) {
            ridesSum += rides;
        }

        double distanceAvg = distanceSum / distanceList.size();
        int batteryAvg = batterySum / batteryList.size();
        double pollutionAvg = pollutionSum / pollutionList.size();
        double ridesAvg = ridesSum / ridesList.size();

        ReportRides reportRides = new ReportRides(pollutionAvg, ridesAvg, distanceAvg, batteryAvg);

        return reportRides;

    }

    public synchronized Report add(Report report) {

        reportList.add(report);
        System.out.println(report);
        return report;

    }

}
