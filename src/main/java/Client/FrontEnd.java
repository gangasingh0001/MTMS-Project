package Client;

import Replicas.Replica1.Constant.ClientConstant;
import Replicas.Replica1.Constant.ServerConstant;
import Frontend.IFrontEnd;
import Replicas.Replica1.Log.ILogging;
import Replicas.Replica1.Log.Logging;
import Replicas.Replica1.Shared.data.IMovie;
import Replicas.Replica1.Shared.data.IUser;
import Replicas.Replica1.Shared.data.Util;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Logger;

public class FrontEnd {
    ILogging logging;
    Logger logger;
    private IMovie movieService = null;
    private IUser userService = null;
    IFrontEnd movieTicketServiceObj = null;

    int menuSelection = -1;
    String response = null;
    Scanner scanner = null;
    boolean logout = false;
    private String[] args;
    private Service serviceAPI;
    URL url;
    public FrontEnd(
            IUser userService,
            IMovie movieService,
            String[] args) {
        this.userService = userService;
        this.movieService = movieService;
        scanner = new Scanner(System.in);
        logger = Logger.getLogger(Client.class.getName());
        this.args = args;
    }

    public void attachLogging(String userID) {
        logging = new Logging(userID, true, false);
        logger = logging.attachFileHandlerToLogger(logger);
    }
    public void login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your UserID:");
        String userID = scanner.nextLine().trim().toUpperCase();

        while (!this.movieService.validateUserID(userID)) {
            System.out.println("Invalid User ID Please enter again:");
            userID = scanner.nextLine().trim().toUpperCase();
        }

        System.out.println("Login Successful " + userID);

        attachLogging(userID);

        this.userService.setUserID(userID.toUpperCase());
        getUrlRef();
        logger.severe("CustomerID: "+ this.userService.getUserID());
        logger.severe("Server Name: "+ Util.getServerFullNameByCustomerID(this.userService.getUserID()));
        logger.severe("Server PORT: "+ Util.getServerPortByCustomerID(this.userService.getUserID()));

        while (!logout)
            menu();
    }

    public void menu() {
        //Admin specific operations
        if(this.userService.isAdmin()) {
            System.out.println("1. " + ServerConstant.ADD_MOVIE_SLOTS);
            System.out.println("2. " + ServerConstant.REMOVE_MOVIE_SLOTS);
            System.out.println("3. " + ServerConstant.LIST_MOVIE_SHOWS_AVAILABILITY);
        }

        if(!this.userService.isAdmin()) {
            System.out.println("1. "+ClientConstant.BOOK_MOVIE);
            System.out.println("2. "+ClientConstant.GET_BOOKING_SCHEDULE);
            System.out.println("3. "+ClientConstant.CANCEL_MOVIE_TICKET);
            System.out.println("4. "+ClientConstant.EXCHANGE_MOVIE_TICKET);
            System.out.println("5. "+ClientConstant.LOGOUT);
        } else {
            System.out.println("4. "+ClientConstant.BOOK_MOVIE);
            System.out.println("5. "+ClientConstant.GET_BOOKING_SCHEDULE);
            System.out.println("6. "+ClientConstant.CANCEL_MOVIE_TICKET);
            System.out.println("7. "+ClientConstant.EXCHANGE_MOVIE_TICKET);
            System.out.println("8. "+ClientConstant.LOGOUT);
        }

        menuSelection = getMenuInput();
        response = referToSelectedMenuObj();
        System.out.println(response);
    }

    public void getUrlRef() {
        try {
            url = new URL("http://localhost:8085/"+"frontend"+"?wsdl");
            QName qName = new QName("http://Frontend/", "FrontEndService");
            serviceAPI = Service.create(url, qName);
            movieTicketServiceObj = serviceAPI.getPort(IFrontEnd.class); //Port of Interface at which Implementation is running
        } catch (MalformedURLException ex) {
            ex.getStackTrace();
        }
    }

    private int getMenuInput() {
        return scanner.nextInt();
    }

    private int getMovieInput() {
        return scanner.nextInt();
    }

    private int getTheaterInput() {
        return scanner.nextInt();
    }

    private int getBookingCapacityInput() {
        return scanner.nextInt();
    }

    private String getMovieIDInput() {
        return scanner.nextLine();
    }

    private String referToSelectedMenuObj() {
        if(this.userService.isAdmin()) {
            switch (menuSelection) {
                case 1 : {
                    this.movieService.moviesPrompt("Select Movie (e.g 1 or 2 etc)");
                    int selectedMovie = getMovieInput();
                    this.movieService.bookingCapacityPrompt("Enter booking capacity");
                    int bookingCapacity = getBookingCapacityInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    LocalDate today = LocalDate.now();
                    LocalDate afterSevenDays = today.plusDays(7);
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    Date sevenDaysAfter = Date.from(afterSevenDays.atStartOfDay(defaultZoneId).toInstant());
                    if(Util.getSlotDateByMovieID(movieID).compareTo(new Date())>0 && Util.getSlotDateByMovieID(movieID).compareTo(sevenDaysAfter)<0) {
                        if(this.movieService.validateMovieID(movieID)) {
                            String response = movieTicketServiceObj.addMovieSlots(movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, this.userService.getUserID());
                            logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, response));
                            return response;
                        }else {
                            logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, "Incorrect movie ID"));
                            break;
                        }
                    }else if(Util.getSlotDateByMovieID(movieID).compareTo(new Date())<0){
                        logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, "Cannot add slot for already finished movie shows"));
                        return "Cannot add slot for already finished movie shows";
                    } else {
                        logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, "Cannot add slot for more than one week ahead"));
                        return "Cannot add slot for more than one week ahead";
                    }
                }
                case 2 : {
                    String res;
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    if(Util.getSlotDateByMovieID(movieID).compareTo(new Date())>0){
                        res = movieTicketServiceObj.removeMovieSlots(movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),this.userService.getUserID());
                        logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                        return res;
                    }
                    res = "Movie cannot be removed for the past show";
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 3 : {
                    String res;
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    res = movieTicketServiceObj.listMovieShowsAvailability(this.movieService.getMovieName(selectedMovie).toUpperCase(), this.userService.getUserID());
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), null, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 4 : {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter CustomerID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();
                    while (!this.movieService.validateUserID(customerID)) {
                        System.out.println("Invalid User ID Please enter again:");
                        customerID = scanner.nextLine().trim().toUpperCase();
                    }
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    this.movieService.bookingCapacityPrompt("Enter no of tickets you want to book");
                    int bookingCapacity = getBookingCapacityInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServiceObj.bookMovieTickets(customerID,movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),bookingCapacity,this.userService.getUserID());
                    logger.severe(Util.createLogMsg(customerID, movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, res));
                    return res;
                }
                case 5 : {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter CustomerID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();
                    while (!this.movieService.validateUserID(customerID)) {
                        System.out.println("Invalid User ID Please enter again:");
                        customerID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServiceObj.getBookingSchedule(customerID,this.userService.getUserID());
                    logger.severe(Util.createLogMsg(customerID,null, null, -1, res));
                    return res;
                }
                case 6 : {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter CustomerID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();
                    while (!this.movieService.validateUserID(customerID)) {
                        System.out.println("Invalid User ID Please enter again:");
                        customerID = scanner.nextLine().trim().toUpperCase();
                    }
                    this.movieService.moviesPrompt("Select movie to cancel booking");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServiceObj.cancelMovieTickets(customerID,movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),0,this.userService.getUserID());
                    logger.severe(Util.createLogMsg(customerID,movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 7 : {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter Customer ID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();

                    while (!this.movieService.validateUserID(customerID)) {
                        System.out.println("Invalid User ID Please enter again:");
                        customerID = scanner.nextLine().trim().toUpperCase();
                    }

                    this.movieService.moviesPrompt("Select a Movie to exchange ticket");
                    int oldSelectedMovie = getMovieInput();
                    scanner.nextLine();

                    System.out.println("Please enter current MovieID to exchange ticket (e.g ATWM190120)");
                    String movieID = getMovieIDInput();

                    this.movieService.theaterPrompt("Select new Theater");
                    int selectedTheater = getTheaterInput();
                    scanner.nextLine();

                    this.movieService.moviesPrompt("Select a new Movie");
                    int newSelectedMovie = getMovieInput();
                    scanner.nextLine();

                    System.out.println("Please enter MovieID (e.g ATWM190120) for movie: "+ this.movieService.getMovieName(newSelectedMovie).toUpperCase() + " at Theater: "+this.movieService.getTheaterName(selectedTheater).toUpperCase());
                    String newMovieID = getMovieIDInput();

                    res = movieTicketServiceObj.exchangeTicket(customerID,movieID,this.movieService.getMovieName(oldSelectedMovie).toUpperCase(),newMovieID,this.movieService.getMovieName(newSelectedMovie).toUpperCase(),this.userService.getUserID());
                    logger.severe(Util.createLogMsg(customerID,movieID, this.movieService.getMovieName(oldSelectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 8 : {
                    logout = true;
                    return null;
                }
            }
        } else {
            switch (menuSelection) {
                case 1 : {
                    String res;
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    this.movieService.bookingCapacityPrompt("Enter no of tickets you want to book");
                    int bookingCapacity = getBookingCapacityInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServiceObj.bookMovieTickets(this.userService.getUserID(),movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),bookingCapacity, this.userService.getUserID());
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, res));
                    return res;
                }
                case 2 : {
                    String res;
                    res = movieTicketServiceObj.getBookingSchedule(this.userService.getUserID(),this.userService.getUserID());
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), null, null, -1, res));
                    return res;
                }
                case 3 : {
                    String res;
                    this.movieService.moviesPrompt("Select movie to cancel booking");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServiceObj.cancelMovieTickets(this.userService.getUserID(),movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),0,this.userService.getUserID());
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 4 : {
                    String res;
                    this.movieService.moviesPrompt("Select a Movie to exchange ticket");
                    int oldSelectedMovie = getMovieInput();
                    scanner.nextLine();

                    System.out.println("Please enter current MovieID to exchange ticket (e.g ATWM190120)");
                    String movieID = getMovieIDInput();

                    this.movieService.theaterPrompt("Select new Theater");
                    int selectedTheater = getTheaterInput();
                    scanner.nextLine();

                    this.movieService.moviesPrompt("Select a new Movie");
                    int newSelectedMovie = getMovieInput();
                    scanner.nextLine();

                    System.out.println("Please enter MovieID (e.g ATWM190120) for movie: "+ this.movieService.getMovieName(newSelectedMovie).toUpperCase() + " at Theater: "+this.movieService.getTheaterName(selectedTheater).toUpperCase());
                    String newMovieID = getMovieIDInput();

                    res = movieTicketServiceObj.exchangeTicket(this.userService.getUserID(),movieID,this.movieService.getMovieName(oldSelectedMovie).toUpperCase(),newMovieID,this.movieService.getMovieName(newSelectedMovie).toUpperCase(),this.userService.getUserID());
                    logger.severe(Util.createLogMsg(this.userService.getUserID(),movieID, this.movieService.getMovieName(oldSelectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 5 : {
                    logout = true;
                    return null;
                }
            }
        }

        return null;
    }
}
