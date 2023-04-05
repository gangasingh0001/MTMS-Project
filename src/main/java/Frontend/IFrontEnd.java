package Frontend;

import javax.jws.WebMethod;

public interface IFrontEnd {
    public void rmIsDown(int rmNumber);
    public void rmHasBug(int rmNumber);
    //public void resendRequest(int retryNumber);

    String addMovieSlots(String movieId, String movieName, int bookingCapacity) ;

    String removeMovieSlots(String movieId, String movieName);

    String listMovieShowsAvailability(String movieName) ;


    String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets);

    String getBookingSchedule(String customerID) ;

    String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) ;

    public String exchangeTicket(String customerID, String movieID, String movieName, String newMovieID, String newMovieName);
}
