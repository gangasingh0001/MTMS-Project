package Frontend;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RequestBuilder {
    InetAddress localHost = null;
    private String customerID = null;
    private String movieID = null;
    private String movieName = null;
    private String newMovieID = null;
    private String newMovieName = null;
    private int bookingCapacity = 0;
    private int sequenceNumber = 0;
    private String IPAddress = null;
    private int retryCount = 1;
    private String invokedMethod = null;

    public RequestBuilder(String invokedMethod, String customerID, String movieID, String movieName, String newMovieID, int bookingCapacity, String newMovieName) {
        this.bookingCapacity = bookingCapacity;
        this.invokedMethod =invokedMethod;
        this.IPAddress = getLocalHost().getHostAddress();
        this.customerID = customerID;
        this.movieID = movieID;
        this.movieName = movieName;
        this.newMovieID = newMovieID;
        this.newMovieName = newMovieName;
    }

    public InetAddress getLocalHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (Exception ex) {
             ex.getStackTrace();
        }
        return null;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String toString() {
        return getSequenceNumber() + ";" ;//+
//                ().toUpperCase() + ";" +
//                getMessageType().toUpperCase() + ";" +
//                getFunction().toUpperCase() + ";" +
//                getClientID().toUpperCase() + ";" +
//                getEventID().toUpperCase() + ";" +
//                getEventType().toUpperCase() + ";" +
//                getOldEventID().toUpperCase() + ";" +
//                getOldEventType().toUpperCase() + ";" +
//                getBookingCapacity();
    }
}
