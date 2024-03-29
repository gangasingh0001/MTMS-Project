package Replicas.Replica2.Shared.data;

public interface IMovie {
    public void moviesPrompt(String heading);
    public void slotsPrompt(String heading);
    public void theaterPrompt(String heading);
    public void bookingCapacityPrompt(String heading);
    public boolean validateMovieID(String movieID);
    public String getMovieName(int movieIndex);
    public String getTheaterName(int theaterIndex);
    public boolean validateUserID(String userID);
    public String grepServerPrefixByMovieID(String movieID);
}
