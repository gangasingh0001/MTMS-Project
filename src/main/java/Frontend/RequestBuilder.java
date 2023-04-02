package Frontend;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RequestBuilder {
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
        this.IPAddress =
        this.customerID = customerID;
        this.movieID = movieID;
        this.movieName = movieName;
        this.newMovieID = newMovieID;
        this.newMovieName = newMovieName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getBookingCapacity() {
        return bookingCapacity;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getNewMovieName() {
        return newMovieName;
    }

    public String getNewMovieID() {
        return this.newMovieID;
    }

    public String getCustomerID() {
        return this.customerID;
    }

    public String getMovieName() {
        return this.movieName;
    }

    public String getMovieID() {
        return this.movieID;
    }

    public String getInvokedMethod() {
        return this.invokedMethod;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String requestBuilderString() {
        return getSequenceNumber() + ";" +
                getInvokedMethod().toUpperCase() + ";" +
                getMovieID().toUpperCase() + ";" +
                getMovieName().toUpperCase() + ";" +
                getNewMovieID().toUpperCase() + ";" +
                getNewMovieName().toUpperCase() + ";" +
                getBookingCapacity();
    }
}
