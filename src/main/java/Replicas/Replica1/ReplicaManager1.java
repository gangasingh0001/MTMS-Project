package Replicas.Replica1;
//
import Replicas.Replica1.Service.IMovieTicket;
import Util.Constants;
import Util.MessageDataModel;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.PriorityQueue;

public class ReplicaManager1 {
    private MulticastSocket socket;
    private InetAddress multicastAddress;
    private int multicastPort;
    static IMovieTicket movieTicketServiceObj = null;
    static URL url;
    private static Service serviceAPI;
    static int Replica_1_Port = 8080;
    PriorityQueue<MessageDataModel> messageQueue = new PriorityQueue<>();

    //public static ConcurrentHashMap<Integer, Message> message_list = new ConcurrentHashMap<>();
    //public static Queue<Message> message_q = new ConcurrentLinkedQueue<Message>();

    public ReplicaManager1(InetAddress multicastAddress, int multicastPort) throws IOException {
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
            System.out.println("Listening for request from Sequencer");
            socket.receive(packet);

            String message = new String(packet.getData(), 0,packet.getLength());
            String[] parts = message.split(";");

            // Get the data from the packet as a byte array
            byte[] data = packet.getData();

            // Extract the sequence number and request data from the packet
            int sequenceNumber = extractSequenceNumber(parts);
            byte[] requestData = extractRequestData(data);

            if(sequenceNumber==-1 && parts[1].equals("RM1")) { //RM has bug
                ProcessBuilder pb = new ProcessBuilder("bash", "/Users/gangasingh/Desktop/COMP6231/MTBS-Project/src/main/java/restartServer.sh");
                // Start the process
                Process p = pb.start();

                // Wait for the process to finish
                try {
                    int exitCode = p.waitFor();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                while (!messageQueue.isEmpty()) {
                    MessageDataModel nextMessage = messageQueue.poll();
                    requestToServers(nextMessage);
                }
            } if(sequenceNumber==-2 && parts[1].equals("RM1")) { //RM has crashed
                ProcessBuilder pb = new ProcessBuilder("bash", "/Users/gangasingh/Desktop/COMP6231/MTBS-Project/src/main/java/restartServer.sh");
                // Start the process
                Process p = pb.start();

                // Wait for the process to finish
                try {
                    int exitCode = p.waitFor();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                while (!messageQueue.isEmpty()) {
                    MessageDataModel nextMessage = messageQueue.poll();
                    requestToServers(nextMessage);
                }
            } else if(sequenceNumber!=-1 && sequenceNumber!=-2){
                MessageDataModel msg = new MessageDataModel(parts[1], parts[3], parts[4], parts[5], parts[6], Integer.valueOf(parts[7]), Integer.valueOf(parts[0]), parts[2]);
                messageQueue.add(msg);
                requestToServers(msg);
            }

            // TODO: Process the request and send a response back to the FrontEnd
            //sendResultToFrontEnd("Response From Replica 1 ", Constants.FE_IPAddress, Constants.FE_Port);

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
        Replicas.Replica1.ReplicaManager1 replicaManager = new Replicas.Replica1.ReplicaManager1(InetAddress.getByName(Constants.RM1_IPAddress), 5001);

        // Start listening for incoming requests
        replicaManager.listen();
    }

    public static void sendResultToFrontEnd(String message, String FrontIpAddress, int FrontEndPort) {
        System.out.println("Result to frontEnd from Replica 1 : " + message);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] bytes = message.getBytes();
            InetAddress aHost = InetAddress.getByName(FrontIpAddress);

            System.out.println(aHost);

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
    private static void requestToServers(MessageDataModel input) {
        int portNumber = Replicas.Replica1.Shared.data.Util.getServerPortByCustomerID(input.customerID.substring(0, 3));
        //movieTicketServiceObj = serviceAPI.getPort(IMovieTicket.class); //Port of Interface at which Implementation is running

        try {
            url = new URL("http://localhost:"+Replica_1_Port+"/"+Replicas.Replica1.Shared.data.Util.getServerFullNameByCustomerID(input.customerID)+"?wsdl");
            QName qName = new QName("http://Service.Replica1.Replicas/", "MovieTicketService");
            serviceAPI = Service.create(url, qName);
            movieTicketServiceObj = serviceAPI.getPort(IMovieTicket.class); //Port of Interface at which Implementation is running
        } catch (MalformedURLException ex) {
            ex.getStackTrace();
        }

        if (input.customerID.substring(3, 4).equalsIgnoreCase("A")) {
            if (input.invokedMethod.equalsIgnoreCase("addMovieSlots")) {
                String response = movieTicketServiceObj.addMovieSlots(input.movieID, input.movieName, input.bookingCapacity);
                sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                //return response;
            } else if (input.invokedMethod.equalsIgnoreCase("removeMovieSlots")) {
                String response = movieTicketServiceObj.removeMovieSlots(input.movieID, input.movieName);
                sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                //return response;
            } else
            if (input.invokedMethod.equalsIgnoreCase("listMovieShowsAvailability")) {
                String response = movieTicketServiceObj.listMovieShowsAvailability(input.movieName);
                sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                //return response;
            }
        }
        else if (input.customerID.substring(3, 4).equalsIgnoreCase("M")) {
            if (input.invokedMethod.equalsIgnoreCase("bookMovieTickets")) {
                String response = movieTicketServiceObj.bookMovieTickets(input.customerID, input.movieID, input.movieName,input.bookingCapacity);
                sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                //return response;
            } else if (input.invokedMethod.equalsIgnoreCase("getBookingSchedule")) {
                String response = movieTicketServiceObj.getBookingSchedule(input.customerID);
                sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                //return response;
            } else if (input.invokedMethod.equalsIgnoreCase("cancelMovieTickets")) {
                String response = movieTicketServiceObj.cancelMovieTickets(input.customerID, input.movieID, input.movieName,input.bookingCapacity);
                sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                //return response;
            } else if (input.invokedMethod.equalsIgnoreCase("exchangeTicket")) {
                String response = movieTicketServiceObj.exchangeTicket(input.customerID, input.movieID, input.movieName, input.newMovieID, input.newMovieName);
                sendResultToFrontEnd(responseBuilderString(input,response), Constants.FE_IPAddress, Constants.FE_Port);
                //return response;
            }
        }
        //return "Null response from server" + input.customerID.substring(0, 3);
    }

    public static String responseBuilderString(MessageDataModel msg, String response) {
        return msg.sequenceNumber + ";" +
                1 + ";" +
                response;
    }
}
