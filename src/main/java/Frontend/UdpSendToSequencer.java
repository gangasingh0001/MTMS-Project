package Frontend;

import Util.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpSendToSequencer {
    private DatagramSocket socket;
    private InetAddress sequencerAddress;
    private int sequencerPort;

    public UdpSendToSequencer(InetAddress sequencerAddress, int sequencerPort) {
        try {
            // Create a DatagramSocket for sending requests to the Sequencer
            this.socket = new DatagramSocket(5020);

            // Save the address and port of the Sequencer
            this.sequencerAddress = sequencerAddress;
            this.sequencerPort = sequencerPort;
        } catch (SocketException ex) {
            ex.getStackTrace();
        }
    }

    public int sendRequest(String request) {
        try {
            // Create a DatagramPacket containing the request
            byte[] requestData = request.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, InetAddress.getLocalHost(), sequencerPort);

            // Send the request packet to the Sequencer
            socket.send(requestPacket);

            byte[] responseData = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
            socket.receive(responsePacket);

            // Process response
            String response = new String(responsePacket.getData(), responsePacket.getOffset(), responsePacket.getLength());
            System.out.println("Received sequence number from Sequencer: " + Integer.valueOf(response));

            return Integer.valueOf(response);
        } catch (IOException ex) {
            ex.getStackTrace();
        }
        return 0;
    }

//    public static void main(String[] args) throws IOException {
//         //Set up the Frontend to send requests to the Sequencer at IP address 192.168.1.100, port 5000
//        UdpSendToSequencer frontend = new UdpSendToSequencer(InetAddress.getByName(Constants.Sequencer_IPAddress), Constants.Sequencer_Port);
//
//        // Send a request to the Sequencer
//        frontend.sendRequest("hello world");
//    }
}

