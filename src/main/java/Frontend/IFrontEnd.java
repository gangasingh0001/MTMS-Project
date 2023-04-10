package Frontend;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@WebService
@SOAPBinding(style= SOAPBinding.Style.RPC)
public interface IFrontEnd {
    public void rmIsDown(int rmNumber);
    public void rmHasBug(int rmNumber);
    //public void resendRequest(int retryNumber);

    @WebMethod()
    String addMovieSlots(String movieId, String movieName, int bookingCapacity, String adminID) ;

    @WebMethod()
    String removeMovieSlots(String movieId, String movieName, String adminID);

    @WebMethod()
    String listMovieShowsAvailability(String movieName, String adminID) ;


    @WebMethod()
    String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets, String adminID);

    @WebMethod()
    String getBookingSchedule(String customerID, String adminID) ;

    @WebMethod()
    String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets, String adminID) ;

    @WebMethod()
    String exchangeTicket(String customerID, String movieID, String movieName, String newMovieID, String newMovieName, String adminID);
}
