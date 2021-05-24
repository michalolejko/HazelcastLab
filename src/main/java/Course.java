import java.io.Serializable;
import java.util.Date;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String destination, departure;
    private int hour;

    public Course(String destination, String departure, int hour){
        this.hour=hour;
        this.departure=departure;
        this.destination=destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public String toString() { return "Course from " + departure + " to " + destination + " at " + hour; }
}
