package Frontend;

import Replicas.Replica1.Shared.data.IMovie;
import Replicas.Replica1.Shared.data.IUser;
import Util.Constants;
import Util.MessageResponseDataModel;
import Util.sortRmResponses;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@WebService(endpointInterface = "Frontend.IFrontEnd")
public class FrontEnd implements IFrontEnd{
    private long startTime;
    private CountDownLatch latch;
    private int bugCount = 3;
    private static long DYNAMIC_TIMEOUT = 10000;
    private final List<String> responses = new ArrayList<>();
//    private IMovie movieService = null;
//    private IUser userService = null;
    UdpSendToSequencer frontend = null;


    UdpRecieveFromReplicaManager udpRecieveFromReplicaManager;

    public FrontEnd() {

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

    public int sendRequestToSequencer(RequestBuilder request) {
        // Send a request to the Sequencer
        return frontend.sendRequest(request.requestBuilderString());
    }

    @Override
    public void rmIsDown(int rmNumber) {
        List<InetAddress> listAllBroadcastAddresses = listAllBroadcastAddresses();
        String rm = "";
        if(rmNumber==1) rm="RM1";
        if(rmNumber==2) rm="RM2";
        if(rmNumber==3) rm="RM3";
        String dataInString = -2 + ";" +
                rm;// Rm

        byte[] message = dataInString.getBytes();
        broadcastMessageToRm(listAllBroadcastAddresses,message,Constants.RM2_Port);
    }

    @Override
    public void rmHasBug(int rmNumber) {
        List<InetAddress> listAllBroadcastAddresses = listAllBroadcastAddresses();
        String rm = "";
        if(rmNumber==1) rm="RM1";
        if(rmNumber==2) rm="RM2";
        if(rmNumber==3) rm="RM3";
        String dataInString = -1 + ";" +
                rm;// Rm

        byte[] message = dataInString.getBytes();
        broadcastMessageToRm(listAllBroadcastAddresses,message,Constants.RM2_Port);
    }

    @Override
    public String addMovieSlots(String movieId, String movieName, int bookingCapacity, String adminID) {
        RequestBuilder myRequest = new RequestBuilder("addMovieSlots",adminID,movieId,movieName,null,bookingCapacity,null);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:addMovieSlots>>>" + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public String removeMovieSlots(String movieId, String movieName, String adminID) {
        RequestBuilder myRequest = new RequestBuilder("removeMovieSlots",adminID,movieId,movieName,null,-1,null);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:removeMovieSlots>>>" + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public String listMovieShowsAvailability(String movieName, String adminID) {
        RequestBuilder myRequest = new RequestBuilder("listMovieShowsAvailability",adminID,null,movieName,null,-1,null);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:listMovieShowsAvailability>>>" + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets, String adminID) {
        RequestBuilder myRequest = new RequestBuilder("bookMovieTickets",adminID,movieId,movieName,null,numberOfTickets,null);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:bookMovieTickets>>>" + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public String getBookingSchedule(String customerID, String adminID) {
        RequestBuilder myRequest = new RequestBuilder("getBookingSchedule",adminID,null,null,null,-1,null);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:getBookingSchedule>>>" + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets, String adminID) {
        RequestBuilder myRequest = new RequestBuilder("cancelMovieTickets",adminID,movieID,movieName,null,numberOfTickets,null);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:cancelMovieTickets>>>" + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public String exchangeTicket(String customerID, String movieID, String movieName, String newMovieID, String newMovieName, String adminID) {
        RequestBuilder myRequest = new RequestBuilder("exchangeTicket",adminID,movieID,movieName,newMovieID,-1,newMovieName);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:exchangeTicket>>>" + myRequest.toString());
        return validateResponses(myRequest);
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
        for(int i=0;i<msgResponse.size();i++) {
            if(msgResponse.get(i).sequenceNumber==myRequest.getSequenceNumber()) {
                filteredList.add(msgResponse.get(i));
            }
        }

        for(int i=0;i<filteredList.size();i++) {
            udpRecieveFromReplicaManager.removeProcessedResponses(filteredList.get(i));
        }
        filteredList = sortRmResponses.sortRm(filteredList);
        // check if RM is down
        System.out.println("Response list size: "+ filteredList.size());
        if(filteredList.size()<3) {
            if(filteredList.get(0).replicaManager==1) {
                if(filteredList.get(1).replicaManager==2) {
                    rmIsDown(3);
                    return "Replica: "+3+" is down. Informing RM's to counter the downtime and attach to new server";
                }
                if(filteredList.get(1).replicaManager==3) {
                    rmIsDown(2);
                    return "Replica: "+2+" is down .Informing RM's to counter the downtime and attach to new server";
                }
            } else if(filteredList.get(0).replicaManager==2) {
                if(filteredList.get(1).replicaManager==3) {
                    rmIsDown(1);
                    return "Replica: "+1+" is down. Informing RM's to counter the downtime and attach to new server";
                }
            }
        } else if(filteredList.get(0).response.equals(filteredList.get(1).response)) {
                if(filteredList.get(1).response.equals(filteredList.get(2).response)) {
                    System.out.println("All responses matched");
                    return filteredList.get(0).response;
                } else {
                    //rmHasBug(3);
                    // Rm 3 has bug
                    rmHasBug(3);

//                    if(bugCount==1) {
//                        bugCount = 3;
//                    }
//                    else
//                        bugCount--;
                    System.out.println("Bug in RM 3");
                    return filteredList.get(1).response;
                }
            } else if(filteredList.get(0).response.equals(filteredList.get(2).response)){
                // RM 1 has bug
                if(bugCount==1) {
                    rmHasBug(1);
                    bugCount = 3;
                }
                else {
                    bugCount--;
                }
                System.out.println("Bug in RM 1");
                return filteredList.get(0).response;
            } else {
                // RM 2 has bug
                if(bugCount==1) {
                    rmHasBug(2);
                    bugCount = 3;
                }
                else
                    bugCount--;
                System.out.println("Bug in RM 2");
                return filteredList.get(1).response;
            }
        return "All servers are down or packet is lost";
    }

    private void broadcastMessageToRm(List<InetAddress> listAllBroadcastAddresses, byte[] sequencedData, int multicastPort) {
        for(InetAddress addr: listAllBroadcastAddresses) {
            DatagramSocket socketInner;
            try {
                socketInner = new DatagramSocket();
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            try {
                socketInner.setBroadcast(true);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            DatagramPacket packet
                    = new DatagramPacket(sequencedData, sequencedData.length, addr, multicastPort);
            try {
                socketInner.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            socketInner.close();
        }
    }

    private List<InetAddress> listAllBroadcastAddresses() {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            try {
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }
}
