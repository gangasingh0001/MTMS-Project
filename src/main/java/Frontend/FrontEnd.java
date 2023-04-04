package Frontend;

import Util.Constants;

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
    @Override
    public void rmIsDown(int rmNumber) {

    }

    public int sendRequestToSequencer(RequestBuilder request) {
        UdpSendToSequencer frontend = null;
        try {
            frontend = new UdpSendToSequencer(InetAddress.getByName(Constants.Sequencer_IPAddress), Constants.Sequencer_Port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // Send a request to the Sequencer
        return frontend.sendRequest(request.toString());
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
        RequestBuilder myRequest = new RequestBuilder("listMovieShowsAvailability",null,null,movieName,null,-1,null);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:listMovieShowsAvailability>>>" + myRequest.toString());
        return "validateResponses(myRequest)";
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
}
