package REST.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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

    public synchronized int getLastReport() {

        if (reportList.size() == 0) {
            return 0;
        } else {
            return (reportList.get(reportList.size()-1)).getId();
        }

    }

    public synchronized Report add(Report report) {

        report.setId();
        reportList.add(report);
        return report;

    }

}
