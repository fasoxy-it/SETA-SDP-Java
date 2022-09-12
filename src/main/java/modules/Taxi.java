package modules;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Taxi {

    private int id;
    private String ip;
    private int port;
    private Integer[] position = {null, null};

    public Taxi() {}

    public String toString() { return this.id + " " + this.ip + " " + this.port + " " + this.position[0] + " " + this.position[1]; }

    public int getId() { return id; }

    public String getIp() { return ip; }

    public int getPort() { return port; }

    public Integer[] getPosition() { return position; }

    public void setPosition(Integer[] position) {
        this.position = position;
    }

}
