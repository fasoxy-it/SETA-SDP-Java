package modules;

import simulators.Buffer;
import simulators.Measurement;

import java.util.ArrayList;
import java.util.List;

public class TaxiBuffer implements Buffer {

    private List<Measurement> measurements;
    private Taxi taxi;

    public TaxiBuffer(Taxi taxi) {
        measurements = new ArrayList<>();
        this.taxi = taxi;
    }

    @Override
    public void addMeasurement(Measurement measurement) {

        if (measurements.size() < 8) {
            measurements.add(measurement);
            System.out.println("Measurement add: " + measurement);
        } else {
            readAllAndClean();
        }

    }

    @Override
    public List<Measurement> readAllAndClean() {
        System.out.println("Measurement Read and Clean!");

        double sum = 0.0;

        for (Measurement measurement : measurements) {
            sum += measurement.getValue();
        }

        double avg = sum / 8;

        taxi.addPM10AverageToPM10Averages(avg);

        int overlappingElement = (int) (8 * 0.5);

        for (int i=0; i<overlappingElement; i++) {
            measurements.remove(i);
        }

        return null;
    }
}
