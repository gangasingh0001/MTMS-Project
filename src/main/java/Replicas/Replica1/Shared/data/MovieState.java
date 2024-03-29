package Replicas.Replica1.Shared.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MovieState {
    private final String movieID;
    private Map<String,Integer> movieNamesAndTickets;
    private final String movieSlot;
    private Date movieDate;
    private final String movieTheatrePrefix;
    public MovieState(String movieName,
                      String movieID,
                      int noOfTicketsBooked) {
        this.movieID = movieID;
        this.addMovieToSlot(movieName,noOfTicketsBooked);
        this.movieSlot = movieID.substring(3,4).toUpperCase();
        this.movieTheatrePrefix = movieID.substring(0,3).toUpperCase();
        this.setMovieDateUTC(movieID.substring(4,10).toUpperCase());
    }

    public void setMovieDateUTC(String date) {
        try {
            this.movieDate = new SimpleDateFormat("ddMMyy").parse(date);
        } catch (ParseException ex) {
            ex.getStackTrace();
        }
    }

    public String getMovieID() {
        return movieID;
    }

    public Map<String,Integer> getMovieTicketInfo() {
        return this.movieNamesAndTickets;
    }

    public void addMovieToExistingSlot(String movieName, Integer noOfTicketsBooked) {
        if(this.movieNamesAndTickets.containsKey(movieName)) {
            int existingBookedTickets = this.movieNamesAndTickets.get(movieName);
            this.movieNamesAndTickets.put(movieName,noOfTicketsBooked+existingBookedTickets);
            return;
        }
        this.movieNamesAndTickets.put(movieName,noOfTicketsBooked);
    }

    public void addMovieToSlot(String movieName, Integer noOfTicketsBooked) {
        if(this.movieNamesAndTickets==null) {
            this.movieNamesAndTickets = new ConcurrentHashMap<>();
            this.movieNamesAndTickets.put(movieName,noOfTicketsBooked);
        }else {
            this.movieNamesAndTickets.put(movieName,noOfTicketsBooked);
        }
    }

    public String getMovieSlot() {
        return movieSlot;
    }

    public Date getMovieDate() {
        return movieDate;
    }

    public String getMovieTheatrePrefix(){
        return movieTheatrePrefix;
    }

}
