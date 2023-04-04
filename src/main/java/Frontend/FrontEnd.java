package Frontend;

import Replicas.Replica1.Shared.data.IMovie;
import Replicas.Replica1.Shared.data.IUser;
import Util.Constants;
import Util.MessageResponseDataModel;
import Util.sortRmResponses;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FrontEnd implements IFrontEnd{
    private long startTime;
    private CountDownLatch latch;
    private static long DYNAMIC_TIMEOUT = 10000;
    private final List<String> responses = new ArrayList<>();
    private IMovie movieService = null;
    private IUser userService = null;
    UdpSendToSequencer frontend = null;


    UdpRecieveFromReplicaManager udpRecieveFromReplicaManager;

    public FrontEnd(IUser userService,
                    IMovie movieService) {
        this.userService = userService;
        this.movieService = movieService;

        Runnable listenerTask = () -> {
            udpRecieveFromReplicaManager = new UdpRecieveFromReplicaManager(Constants.FE_Port);
            // Start listening for requests
            udpRecieveFromReplicaManager.listen();
        };
        Thread listenerThread = new Thread(listenerTask);
        listenerThread.start();
        try {
            frontend = new UdpSendToSequencer(InetAddress.getByName(Constants.Sequencer_IPAddress), Constants.Sequencer_Port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void rmIsDown(int rmNumber) {

    }
    public int sendRequestToSequencer(RequestBuilder request) {
        // Send a request to the Sequencer
        return frontend.sendRequest(request.requestBuilderString());
    }

    @Override
    public void rmHasBug(int rmNumber) {

    }

    @Override
    public void resendRequest(int retryNumber) {

    }

    @Override
    public String addMovieSlots(String movieId, String movieName, int bookingCapacity) {
        return null;
    }

    @Override
    public String removeMovieSlots(String movieId, String movieName) {
        return null;
    }

    @Override
    public String listMovieShowsAvailability(String movieName) {
        RequestBuilder myRequest = new RequestBuilder("listMovieShowsAvailability",this.userService.getUserID(),null,movieName,null,-1,null);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:listMovieShowsAvailability>>>" + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets) {
        return null;
    }

    @Override
    public String getBookingSchedule(String customerID) {
        return null;
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        return null;
    }

    @Override
    public String exchangeTicket(String customerID, String movieID, String movieName, String newMovieID, String newMovieName) {
        return null;
    }

    private int sendUdpUnicastToSequencer(RequestBuilder myRequest) {
        //startTime = System.nanoTime();
        int sequenceNumber = this.sendRequestToSequencer(myRequest);
        myRequest.setSequenceNumber(sequenceNumber);
        latch = new CountDownLatch(3);
        waitForResponse();
        return sequenceNumber;
    }

    public void waitForResponse() {
        try {
            System.out.println("FE Implementation:waitForResponse>>>ResponsesRemain" + latch.getCount());
            boolean timeoutReached = latch.await(DYNAMIC_TIMEOUT, TimeUnit.MILLISECONDS);
            if (timeoutReached) {
                setDynamicTimout();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setDynamicTimout() {
        if (DYNAMIC_TIMEOUT < 4000) {
            DYNAMIC_TIMEOUT = (DYNAMIC_TIMEOUT + (DYNAMIC_TIMEOUT * 3)) / 2;
        } else {
            DYNAMIC_TIMEOUT = 10000;
        }
        System.out.println("FE Implementation:setDynamicTimout>>>" + DYNAMIC_TIMEOUT);
    }

    public String validateResponses(RequestBuilder myRequest) {
        List<MessageResponseDataModel> msgResponse = udpRecieveFromReplicaManager.getResponses();
        List<MessageResponseDataModel> filteredList = new ArrayList<>();
        for(MessageResponseDataModel msg:msgResponse) {
            if(msg.sequenceNumber==myRequest.getSequenceNumber()) {
                filteredList.add(msg);
            }
        }

        // check if RM is down
        if(filteredList.size()<3) {
            System.out.println("System is down");
        } else {
            filteredList = sortRmResponses.sortRm(filteredList);
            if(filteredList.get(0).response.equals(filteredList.get(1).response)) {
                if(filteredList.get(1).response.equals(filteredList.get(2))) {
                    return filteredList.get(0).response;
                } else {
                    // Rm 3 has bug
                    System.out.println("Bug in RM 3");
                    return filteredList.get(1).response;
                }
            } else if(filteredList.get(0).response.equals(filteredList.get(2).response)){
                // RM 1 has bug
                System.out.println("Bug in RM 1");
                return filteredList.get(0).response;
            } else {
                // RM 2 has bug
                System.out.println("Bug in RM 2");
                return filteredList.get(1).response;
            }
        }
        return "All RMs are down";
    }
}
