package Replicas.Replica3;
//
import Replicas.Replica3.Service.IMovieTicket;
import Util.Constants;
//
//import javax.xml.namespace.QName;
//import javax.xml.ws.Service;
//import java.io.IOException;
//import java.net.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class ReplicaManager1 {
//    private static final String Bug_ID = "MTLM8888";
//    public static int lastSequenceID = 1;
//    public static int bug_counter = 0;
//    private static Service serviceAPI;
//    static IMovieTicket movieTicketServiceObj = null;
//    static URL url;
//    //public static ConcurrentHashMap<Integer, Message> message_list = new ConcurrentHashMap<>();
//    //public static Queue<Message> message_q = new ConcurrentLinkedQueue<Message>();
//    private static boolean serversFlag = true;
//    private static boolean BugFlag = true;
//
//    public static void main(String[] args) throws Exception {
//        Run();
//    }
//
//    private static void Run() throws Exception {
//        Runnable task = () -> {
//            try {
//                receive();
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        };
//        Thread thread = new Thread(task);
//        thread.start();
//    }
//
//    private static void receive() throws Exception {
//        MulticastSocket socket = null;
//        try {
//
//            socket = new MulticastSocket();
//
//            socket.joinGroup(InetAddress.getByName("239.0.0.2"));
//
//            byte[] buffer = new byte[1024];
//            System.out.println("RM1 UDP Server Started(port=1234)............");
//
//            while (true) {
//                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
//                socket.receive(request);
//
//                String data = new String(request.getData(), 0, request.getLength());
//
//                String res = requestToServers(data);
//                responseToFrontEnd(res,Constants.FE_IPAddress);
//            }
//
//        } catch (SocketException e) {
//            System.out.println("Socket: " + e.getMessage());
//        } catch (IOException e) {
//            System.out.println("IO: " + e.getMessage());
//        } finally {
//            if (socket != null)
//                socket.close();
//        }
//    }
//
//    //Send RMI request to server
//    private static String requestToServers(String input) throws Exception {
//        String[] parts = input.split(";");
//        int portNumber = Util.getServerPortByCustomerID(parts[1].substring(0, 3));
//        movieTicketServiceObj = serviceAPI.getPort(IMovieTicket.class); //Port of Interface at which Implementation is running
//
//        try {
//            url = new URL("http://localhost:8080/"+Util.getServerFullNameByCustomerID(parts[1])+"?wsdl");
//            QName qName = new QName("http://Replicas.Replica1.Service/", "MovieTicketService");
//            serviceAPI = Service.create(url, qName);
//            movieTicketServiceObj = serviceAPI.getPort(IMovieTicket.class); //Port of Interface at which Implementation is running
//        } catch (MalformedURLException ex) {
//            ex.getStackTrace();
//        }
//
//        if (parts[1].substring(3, 4).equalsIgnoreCase("M")) {
////            if (parts[0].equalsIgnoreCase("addEvent")) {
////                String response = movieTicketServiceObj.addEvent(input.newEventID, input.newEventType, input.bookingCapacity);
////                System.out.println(response);
////                return response;
////            } else if (input.Function.equalsIgnoreCase("removeEvent")) {
////                String response = obj.removeEvent(input.newEventID, input.newEventType);
////                System.out.println(response);
////                return response;
//           // } else
//                if (parts[0].equalsIgnoreCase("listMovieShowsAvailability")) {
//                String response = movieTicketServiceObj.listMovieShowsAvailability(parts[3]);
//                System.out.println(response);
//                return response;
//            }
//        }
////        else if (input.userID.substring(3, 4).equalsIgnoreCase("C")) {
////            if (input.Function.equalsIgnoreCase("bookEvent")) {
////                String response = obj.bookEvent(input.userID, input.newEventID, input.newEventType);
////                System.out.println(response);
////                return response;
////            } else if (input.Function.equalsIgnoreCase("getBookingSchedule")) {
////                String response = obj.getBookingSchedule(input.userID);
////                System.out.println(response);
////                return response;
////            } else if (input.Function.equalsIgnoreCase("cancelEvent")) {
////                String response = obj.cancelEvent(input.userID, input.newEventID, input.newEventType);
////                System.out.println(response);
////                return response;
////            } else if (input.Function.equalsIgnoreCase("swapEvent")) {
////                String response = obj.swapEvent(input.userID, input.newEventID, input.newEventType, input.oldEventID, input.oldEventType);
////                System.out.println(response);
////                return response;
////            }
////        }
//        return "Null response from server" + parts[1].substring(0, 3);
//    }
//
//    public static void responseToFrontEnd(String message, String FrontIpAddress) {
//        System.out.println("Message to front:" + message);
//        DatagramSocket socket = null;
//        try {
//            socket = new DatagramSocket(Constants.FE_Port);
//            byte[] bytes = message.getBytes();
//            InetAddress aHost = InetAddress.getByName(FrontIpAddress);
//
//            System.out.println(aHost);
//            DatagramPacket request = new DatagramPacket(bytes, bytes.length, aHost, 1999);
//            socket.send(request);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (socket != null) {
//                socket.close();
//            }
//        }
//
//    }
//}

import Util.MessageComparator;
import Util.MessageDataModel;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.PriorityQueue;

public class ReplicaManager3 {
    private MulticastSocket socket;
    private InetAddress multicastAddress;
    private int multicastPort;
    static URL url;

    static String urlChanged = "http://Service.Replica3.Replicas/";
    static int Replica_3_Port = 8082;
    private static Service serviceAPI;
    //public static ConcurrentHashMap<Integer, Message> message_list = new ConcurrentHashMap<>();
    PriorityQueue<MessageDataModel> messageQueue = new PriorityQueue<>();

    public ReplicaManager3(InetAddress multicastAddress, int multicastPort) throws IOException {
        // Create a MulticastSocket for receiving requests from the Sequencer
        this.socket = new MulticastSocket(multicastPort);
        NetworkInterface networkInterface = NetworkInterface.getByName("en0");
        Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces();
        if (!networkInterface.supportsMulticast()) {
            networkInterface.supportsMulticast();
            System.out.println("Multicast is not supported on this interface.");
        }
        // Join the multicast group
        this.multicastAddress = multicastAddress;
        this.socket.setNetworkInterface(networkInterface);
        this.socket.joinGroup(multicastAddress);

        // Save the multicast address and port for sending responses back to the Sequencer
        this.multicastPort = multicastPort;
    }

    public void listen() throws IOException {
        // Create a DatagramPacket to hold incoming packets
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        // Loop forever, receiving incoming packets and processing them
        while (true) {
            // Wait for an incoming packet
            System.out.println("Listening for request from Sequencer RM3");
            socket.receive(packet);

            String message = new String(packet.getData(), 0,packet.getLength());
            String[] parts = message.split(";");

            // Get the data from the packet as a byte array
            byte[] data = packet.getData();

            // Extract the sequence number and request data from the packet
            int sequenceNumber = extractSequenceNumber(parts);
            byte[] requestData = extractRequestData(data);

            if(sequenceNumber==-1 && parts[1].equals("RM3")) { //RM has bug
                this.urlChanged = "http://Service.Server.Replicas/";
                this.Replica_3_Port = 8083;
                while (!messageQueue.isEmpty()) {
                    MessageDataModel nextMessage = messageQueue.poll();
                    System.out.println("Invoking method to be operated on new Server");
                    System.out.println(nextMessage.invokedMethod);

                    requestToServers(nextMessage,false);
                }
            } if(sequenceNumber==-2 && parts[1].equals("RM3")) { //RM has bug
                this.urlChanged = "http://Service.Server.Replicas/";
                this.Replica_3_Port = 8083;
                while (!messageQueue.isEmpty()) {
                    MessageDataModel nextMessage = messageQueue.poll();
                    System.out.println("Invoking method to be operated on new Server");
                    System.out.println(nextMessage.invokedMethod);

                    requestToServers(nextMessage,false);
                }
            } else if(sequenceNumber!=-1 && sequenceNumber!=-2){
                MessageDataModel msg = new MessageDataModel(parts[1], parts[3], parts[4], parts[5], parts[6], Integer.valueOf(parts[7]), Integer.valueOf(parts[0]), parts[2]);
                messageQueue.add(msg);
                requestToServers(msg,true);
            }
        }
    }

    private int extractSequenceNumber(String[] data) {
        // TODO: Implement logic to extract sequence number from data
        return Integer.valueOf(data[0]);
    }

    private byte[] extractRequestData(byte[] data) {
        // TODO: Implement logic to extract request data from data
        return data;
    }

    public static void main(String[] args) throws IOException {
        // Set up the Replica Manager to listen for requests from the Sequencer via multicast
        ReplicaManager3 replicaManager = new ReplicaManager3(InetAddress.getByName(Constants.RM1_IPAddress), 5001);

        // Start listening for incoming requests
        replicaManager.listen();
    }

    public static void sendResultToFrontEnd(String message, String FrontIpAddress, int FrontEndPort) {
        System.out.println("Result to frontEnd from Replica 3 : " + message);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] bytes = message.getBytes();
            InetAddress aHost = InetAddress.getByName(FrontIpAddress);

            //System.out.println(aHost);

            DatagramPacket request = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), FrontEndPort);
            socket.send(request);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

    }

    //Send request to server
    private static void requestToServers(MessageDataModel input, boolean respondToFrontend) {
        int portNumber = Replicas.Replica1.Shared.data.Util.getServerPortByCustomerID(input.customerID.substring(0, 3));
        //movieTicketServiceObj = serviceAPI.getPort(IMovieTicket.class); //Port of Interface at which Implementation is running

        if(Replica_3_Port==8083) {
            Replicas.Server.Service.IMovieTicket movieTicketServiceObj = null;
            try {
                url = new URL("http://localhost:"+Replica_3_Port+"/"+Replicas.Replica3.Shared.data.Util.getServerFullNameByCustomerID(input.customerID)+"?wsdl");
                QName qName = new QName(urlChanged, "MovieTicketService");
                serviceAPI = Service.create(url, qName);
                movieTicketServiceObj = serviceAPI.getPort(Replicas.Server.Service.IMovieTicket.class);

            } catch (MalformedURLException ex) {
                ex.getStackTrace();
            }

            if (input.customerID.substring(3, 4).equalsIgnoreCase("A")) {
                if (input.invokedMethod.equalsIgnoreCase("addMovieSlots")) {
                    String response = movieTicketServiceObj.addMovieSlots(input.movieID, input.movieName, input.bookingCapacity);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else if (input.invokedMethod.equalsIgnoreCase("removeMovieSlots")) {

                /*
                Introduced Software bug at particular movieID
                 */
                   // if(input.movieID.equals("ATWM080423")) input.movieID="ATWA070423";



                    String response = movieTicketServiceObj.removeMovieSlots(input.movieID, input.movieName);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else
                if (input.invokedMethod.equalsIgnoreCase("listMovieShowsAvailability")) {
                    String response = movieTicketServiceObj.listMovieShowsAvailability(input.movieName);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                }
            }
            else if (input.customerID.substring(3, 4).equalsIgnoreCase("M")) {
                if (input.invokedMethod.equalsIgnoreCase("bookMovieTickets")) {
                    String response = movieTicketServiceObj.bookMovieTickets(input.customerID, input.movieID, input.movieName,input.bookingCapacity);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else if (input.invokedMethod.equalsIgnoreCase("getBookingSchedule")) {
                    String response = movieTicketServiceObj.getBookingSchedule(input.customerID);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else if (input.invokedMethod.equalsIgnoreCase("cancelMovieTickets")) {
                    String response = movieTicketServiceObj.cancelMovieTickets(input.customerID, input.movieID, input.movieName,input.bookingCapacity);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else if (input.invokedMethod.equalsIgnoreCase("exchangeTicket")) {
                    String response = movieTicketServiceObj.exchangeTicket(input.customerID, input.movieID, input.movieName, input.newMovieID, input.newMovieName);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                }
            }
        } else {
            IMovieTicket movieTicketServiceObj = null;
            try {
                url = new URL("http://localhost:"+Replica_3_Port+"/"+Replicas.Replica3.Shared.data.Util.getServerFullNameByCustomerID(input.customerID)+"?wsdl");
                QName qName = new QName(urlChanged, "MovieTicketService");
                serviceAPI = Service.create(url, qName);
                //Port of Interface at which Implementation is running
                movieTicketServiceObj = serviceAPI.getPort(IMovieTicket.class);
            } catch (MalformedURLException ex) {
                ex.getStackTrace();
            }

            if (input.customerID.substring(3, 4).equalsIgnoreCase("A")) {
                if (input.invokedMethod.equalsIgnoreCase("addMovieSlots")) {
                    String response = movieTicketServiceObj.addMovieSlots(input.movieID, input.movieName, input.bookingCapacity);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else if (input.invokedMethod.equalsIgnoreCase("removeMovieSlots")) {

                /*
                Introduced Software bug at particular movieID
                 */
                    String dummyMovieId;
                    if(input.movieID.equals("ATWA060423")) dummyMovieId="ATWA090423";
                    else dummyMovieId = input.movieID;



                    String response = movieTicketServiceObj.removeMovieSlots(dummyMovieId, input.movieName);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else
                if (input.invokedMethod.equalsIgnoreCase("listMovieShowsAvailability")) {
                    String response = movieTicketServiceObj.listMovieShowsAvailability(input.movieName);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                }
            }
            else if (input.customerID.substring(3, 4).equalsIgnoreCase("M")) {
                if (input.invokedMethod.equalsIgnoreCase("bookMovieTickets")) {
                    String response = movieTicketServiceObj.bookMovieTickets(input.customerID, input.movieID, input.movieName,input.bookingCapacity);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else if (input.invokedMethod.equalsIgnoreCase("getBookingSchedule")) {
                    String response = movieTicketServiceObj.getBookingSchedule(input.customerID);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else if (input.invokedMethod.equalsIgnoreCase("cancelMovieTickets")) {
                    String response = movieTicketServiceObj.cancelMovieTickets(input.customerID, input.movieID, input.movieName,input.bookingCapacity);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                } else if (input.invokedMethod.equalsIgnoreCase("exchangeTicket")) {
                    String response = movieTicketServiceObj.exchangeTicket(input.customerID, input.movieID, input.movieName, input.newMovieID, input.newMovieName);
                    if(respondToFrontend) sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                    //return response;
                }
            }
        }

        //return "Null response from server" + input.customerID.substring(0, 3);
    }

    public static String responseBuilderString(MessageDataModel msg, String response) {
        return msg.sequenceNumber + ";" +
                3 + ";" +
                response;
    }
}
