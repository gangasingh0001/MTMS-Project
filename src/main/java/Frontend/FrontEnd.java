package Frontend;

import Util.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FrontEnd implements IFrontEnd{
    private long startTime;
    private CountDownLatch latch;
    private static long DYNAMIC_TIMEOUT = 10000;
    private final List<String> responses = new ArrayList<>();

    public FrontEnd() {
        listenForUDPResponses();
    }
    @Override
    public void rmIsDown(int rmNumber) {

    }

    @Override
    public int sendRequestToSequencer(RequestBuilder request) {

    }

    @Override
    public void rmHasBug(int rmNumber) {

    }

    @Override
    public void resendRequest(int retryNumber) {

    }

    @Override
    public String addMovieSlots(String movieId, String movieName, int bookingCapacity) {
        RequestBuilder myRequest = new RequestBuilder("addMovieSlots",null,movieId,movieName,null,bookingCapacity,null);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:bookEvent>>>" + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public String removeMovieSlots(String movieId, String movieName) {
        return null;
    }

    @Override
    public String listMovieShowsAvailability(String movieName) {
        return null;
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

    private static void listenForUDPResponses() {
        DatagramSocket aSocket = null;
        try {
            InetAddress desiredAddress = InetAddress.getByName(Constants.FE_IPAddress);
            aSocket = new DatagramSocket(Constants.FE_Port, desiredAddress);
            byte[] buffer = new byte[1000];
            System.out.println("FE Server Started on " + desiredAddress + ":" + Constants.FE_Port + "............");

            while (true) {
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(response);
                String sentence = new String(response.getData(), 0,
                        response.getLength()).trim();
                System.out.println("FE:Response received from Rm>>>" + sentence);
                //RmResponse rmResponse = new RmResponse(sentence);

                System.out.println("Adding response to FrontEndImplementation:");
                responses.add(response);
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}
