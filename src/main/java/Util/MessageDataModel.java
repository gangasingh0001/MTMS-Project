package Util;

public class MessageDataModel implements Comparable<MessageDataModel>{
    public String customerID = null;
    public String movieID = null;
    public String movieName = null;
    public String newMovieID = null;
    public String newMovieName = null;
    public int bookingCapacity = 0;
    public int sequenceNumber = 0;
    public String invokedMethod = null;
    
    public MessageDataModel(String customerID, String movieID, String movieName, String newMovieID, String newMovieName, int bookingCapacity, int sequenceNumber, String invokedMethod) {
        this.bookingCapacity = bookingCapacity;
        this.invokedMethod =invokedMethod;
        this.customerID = customerID;
        this.movieID = movieID;
        this.movieName = movieName;
        this.newMovieID = newMovieID;
        this.newMovieName = newMovieName;
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public int compareTo(MessageDataModel o) {
        return Integer.compare(this.sequenceNumber, o.sequenceNumber);
    }
}
