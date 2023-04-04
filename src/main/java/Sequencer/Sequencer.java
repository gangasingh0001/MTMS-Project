//package Sequencer;
//
//import Util.Constants;
//
//import java.io.IOException;
//import java.net.*;
//
//public class Sequencer {
//    private static int sequencerId = 0;
//    private static final String sequencerIP = "192.168.2.17";
//
//    public static void main(String[] args) {
//        DatagramSocket aSocket = null;
//        try {
//            aSocket = new DatagramSocket(Constants.Sequencer_Port, InetAddress.getByName(Constants.Sequencer_IPAddress));
//            byte[] buffer = new byte[1000];
//            System.out.println("Sequencer UDP Server Started");
//            while (true) {
//                DatagramPacket request = new DatagramPacket(buffer,
//                        buffer.length);
//
//                aSocket.receive(request);
//
//                String sentence = new String(request.getData(), 0,
//                        request.getLength());
//
//                String[] parts = sentence.split(";");
//                int sequencerId1 = Integer.parseInt(parts[0]);
//                String ip = request.getAddress().getHostAddress();
//
//                String sentence1 = ip + ";" +
//                        parts[2] + ";" +
//                        parts[3] + ";" +
//                        parts[4] + ";" +
//                        parts[5] + ";" +
//                        parts[6] + ";" +
//                        parts[7] + ";" +
//                        parts[8] + ";" +
//                        parts[9] + ";";
//
//                System.out.println(sentence1);
//                sendMessage(sentence1, sequencerId1, true);
//
//                byte[] SeqId = (Integer.toString(sequencerId)).getBytes();
//                InetAddress aHost1 = request.getAddress();
//                int port1 = request.getPort();
//
//                System.out.println(aHost1 + ":" + port1);
//                DatagramPacket request1 = new DatagramPacket(SeqId,
//                        SeqId.length, aHost1, port1);
//                aSocket.send(request1);
//            }
//
//        } catch (SocketException e) {
//            System.out.println("Socket: " + e.getMessage());
//        } catch (IOException e) {
//            System.out.println("IO: " + e.getMessage());
//        } finally {
//            if (aSocket != null)
//                aSocket.close();
//        }
//    }
//
//    //Forward request to Replicas
//    public static void sendMessage(String message, int sequencerId1, boolean isRequest) throws IOException {
//        int port = 1234;
//
//        if (sequencerId1 == 0 && isRequest) {
//            sequencerId1 = ++sequencerId;
//        }
//        String finalMessage = sequencerId1 + ";" + message;
//        MulticastSocket socket = new MulticastSocket();
//        InetAddress group = InetAddress.getByName("239.1.1.2");
//        //DatagramSocket aSocket = null;
//        try {
//            //aSocket = new DatagramSocket();
//            byte[] messages = finalMessage.getBytes();
//            //InetAddress aHost = InetAddress.getByName("239.0.0.2"); //TODO: Change this IP to Replica Manager
//
//            DatagramPacket request = new DatagramPacket(messages,
//                    messages.length, group, 1234); //TODO: Change this IP to Replica Manager
//            socket.send(request);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
package Sequencer;
import Util.Constants;

import java.io.IOException;
import java.net.*;

public class Sequencer {
        private DatagramSocket socket;
        private InetAddress multicastAddress;
        private int multicastPort;

        private int sequenceNumber =0;

        public Sequencer(InetAddress multicastAddress, int multicastPort) throws IOException {
            // Create a DatagramSocket for receiving requests from the Frontend
            this.socket = new DatagramSocket(5000);
//            socket.setReuseAddress(true);
//            socket.bind(new InetSocketAddress(Constants.Sequencer_IPAddress, Constants.Sequencer_Port));
            // Save the multicast address and port for sending requests to the Replica Manager
            this.multicastAddress = multicastAddress;
            this.multicastPort = multicastPort;
        }

        public void listen() throws IOException {
            // Create a DatagramPacket to hold incoming packets
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Loop forever, receiving incoming packets and processing them
            while (true) {

            System.out.println("Sequencer Listening for request from Front End at Port: "+ Constants.Sequencer_Port);
            // Wait for an incoming packet
            socket.receive(packet);

            // Get the data from the packet as a byte array
            byte[] data = packet.getData();

            // Create a new packet with a sequence number added to the request
            int sequenceNumber = generateSequenceNumber();
            byte[] sequencedData = addSequenceNumber(sequenceNumber, data);

            sendSequenceToFrontEnd(packet,sequenceNumber);

            DatagramPacket sequencedPacket = new DatagramPacket(sequencedData, sequencedData.length, InetAddress.getLocalHost(), multicastPort);

            System.out.println("Sequencer sending request to RM : MulticastAddress - "+multicastAddress);
            System.out.println("Sequencer sending request to RM : MulticastPort - "+multicastPort);
            // Send the sequenced packet to the Replica Manager
            socket.send(sequencedPacket);
            }
        }

        private int generateSequenceNumber() {
            // TODO: Implement sequence number generation logic
            return ++this.sequenceNumber;
        }

        private byte[] addSequenceNumber(int sequenceNumber, byte[] data) {
            // TODO: Implement logic to add sequence number to data
            return data;
        }

        private void sendSequenceToFrontEnd(DatagramPacket requestPacket, int sequenceNumber) {
            InetAddress clientAddress = null;
            try {
                clientAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            int clientPort = 5020;

            // Send sequence number
            String response = String.valueOf(sequenceNumber);

            byte[] responseData = response.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
            try {
                socket.send(responsePacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static void main(String[] args) throws IOException {
            // Set up the Sequencer to listen for requests from the Frontend and send them to the Replica Manager via multicast
            Sequencer sequencer = new Sequencer(InetAddress.getByName(Constants.RM1_IPAddress), Constants.RM1_Port);

            // Start listening for incoming requests
            sequencer.listen();
        }
}
