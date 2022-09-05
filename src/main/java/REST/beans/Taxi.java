package REST.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Taxi {

    private int id;
    private String ip;
    private int port;

    public Taxi() {};

    public Taxi(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public int getId() { return id; }

    public String getIp() { return ip; }

    public int getPort() { return port; }

}
