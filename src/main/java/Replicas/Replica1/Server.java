package Replicas.Replica1;

import Replicas.Replica1.Constant.ServerConstant;
import Replicas.Replica1.Constant.ServiceConstant;
import Replicas.Replica1.Service.MovieTicket;
import Replicas.Replica1.Shared.Database.ICustomerBooking;
import Replicas.Replica1.Shared.Database.IMovies;
import Replicas.Replica1.Shared.data.IMovie;
import Replicas.Replica1.Shared.data.IServerInfo;
import Replicas.Replica1.Shared.data.IUdp;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

public class Server extends Thread{
    private MovieTicket movieTicketObj = null;
    private String serverID;
    private static String serverName;
    //private static int serverRegistryPort;
    private static int serverPort;
    private final IServerInfo serverInfo;
    private final IUdp udpService;
    private final IMovie movieService;
    private final ICustomerBooking customerBookingDb;
    private final IMovies moviesDb;
    private final Logger logger;
    private String[] args;
    public Server (Logger logger,
                    String serverID,
                   IServerInfo serverInfo,
                   IUdp udpService,
                   IMovie movieService,
                   ICustomerBooking customerBookingDb,
                   IMovies moviesDb,
                   String[] args) throws Exception{
        this.serverID = serverID;
        this.serverInfo = serverInfo;
        this.udpService = udpService;
        this.movieService = movieService;
        this.customerBookingDb = customerBookingDb;
        this.moviesDb = moviesDb;
        this.logger = logger;
        this.args = args;
        getServerInfo();
        movieTicketObj = new MovieTicket(logger,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
        this.startListenerAndRegisterEndPoint();
    }

    private void startListenerAndRegisterEndPoint() {
        Runnable listenerTask = () -> {
            requestlistener(movieTicketObj, serverPort, serverName);
        };

        Endpoint endpoint = Endpoint.publish("http://localhost:8080/"+ serverName,movieTicketObj);
        logger.severe("Endpoint: "+ endpoint.toString());

        Thread listenerThread = new Thread(listenerTask);
        listenerThread.start();
        listenerThread.currentThread().setName(serverName);

        logger.severe("Thread name: "+ listenerThread.currentThread().getName());
        logger.severe("State of thread: " + listenerThread.currentThread().getState());
    }

    public void getServerInfo() {
        switch (serverID) {
            case ServerConstant.SERVER_ATWATER_PREFIX : {
                serverName = ServerConstant.SERVER_ATWATER;
                serverPort = ServerConstant.SERVER_ATWATER_PORT;
                break;
            }
            case ServerConstant.SERVER_VERDUN_PREFIX : {
                serverName = ServerConstant.SERVER_VERDUN;
                serverPort = ServerConstant.SERVER_VERDUN_PORT;
                break;
            }
            case ServerConstant.SERVER_OUTREMONT_PREFIX : {
                serverName = ServerConstant.SERVER_OUTREMONT;
                serverPort = ServerConstant.SERVER_OUTREMONT_PORT;
                break;
            }
            default : {
                break;
            }
            // TODO: Implement Exception Handling if serverID is null.
        }
    }

    private void requestlistener(MovieTicket movieTicketObj, int serverPort, String serverName) {
        String response = "";
        logger.severe("Listener Datagram port : "+serverPort);
        try (DatagramSocket socket = new DatagramSocket(serverPort)) {
            byte[] buffer = new byte[1000];
            while (true) {
                logger.severe("Request Listener initiated for server "+serverName);
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String requestParams = new String(request.getData(), 0, request.getLength());
                String[] requestParamsArray = requestParams.split(";");
                response = methodInvocation(movieTicketObj, requestParamsArray, response);
                byte[] sendData = response.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, response.length(), request.getAddress(),
                        request.getPort());
                socket.send(reply);
            }
        } catch (SocketException socketException) {
            System.out.println("Listener SocketException: " + socketException.getMessage());
        } catch (IOException ioException) {
            System.out.println("Listener IOException: " + ioException.getMessage());
        }
    }

     private String methodInvocation(MovieTicket movieTicketServant, String[] requestParamsArray, String response) {
        String invokedMethod = requestParamsArray[0];
        String customerID = requestParamsArray[1];
        String movieName = requestParamsArray[2];
        String movieID = requestParamsArray[3];

        boolean isRegisteredToServer = Boolean.parseBoolean(requestParamsArray[4]);
        int numberOfTickets = Integer.parseInt(requestParamsArray[4]);
        if (invokedMethod.equalsIgnoreCase(ServiceConstant.getMoviesListInTheatre)) {
            response = movieTicketServant.getMoviesListInTheatre(movieName);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.bookTicket)) {
            response = movieTicketServant.bookTicket(customerID,movieID,movieName,numberOfTickets,isRegisteredToServer);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getCustomerBookingList)) {
            response = movieTicketServant.getCustomerBookingList(customerID);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.cancelTicket)) {
            response = movieTicketServant.cancelTicket(customerID,movieID,movieName,numberOfTickets);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.findNextAvailableSlot)) {
            response = movieTicketServant.findNextAvailableSlot(customerID,movieID,movieName);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getNoOfBookingsInWeek)) {
            response = movieTicketServant.getNoOfBookingsInWeek(customerID,movieID);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.checkSlotAndBook)) {
            response = movieTicketServant.checkSlotAndBook(customerID,movieID,movieName,numberOfTickets);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.ifMovieBookingExist)) {
            response = String.valueOf(movieTicketServant.ifMovieBookingExist(customerID,movieID));
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getNoOfBookings)) {
            response = String.valueOf(movieTicketServant.getNoOfBookings(customerID,movieID, movieName));
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getNoOfBookingSlotAvailable)) {
            response = String.valueOf(movieTicketServant.getNoOfBookingSlotAvailable(movieID, movieName));
        }
//        else if (invokedMethod.equalsIgnoreCase(ServiceConstant.cancelBooking)) {
//            response = String.valueOf(movieTicketServant.cancelBooking(customerID, movieID, movieName));
//        }
        else if (invokedMethod.equalsIgnoreCase(ServiceConstant.decrementBookingSlot)) {
            response = String.valueOf(movieTicketServant.decrementBookingSlot(movieID, movieName, numberOfTickets));
        }
        return response;
    }
}
