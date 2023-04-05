package Frontend;

import Util.MessageResponseDataModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class UdpRecieveFromReplicaManager {
    private DatagramSocket socket;
    private int port;

    private List<MessageResponseDataModel> responses;

    public UdpRecieveFromReplicaManager(int port) {
        // Create a DatagramSocket to listen for requests on the specified port
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        this.port = port;
        this.responses = new ArrayList<MessageResponseDataModel>();
    }

    public void listen() {
        System.out.println("Server listening on port " + port + "...");

        // Loop indefinitely to receive requests
        while (true) {
            // Create a DatagramPacket to hold the received request data
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Receive the request from the client
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Convert the request data to a string
            String response = new String(packet.getData(), 0, packet.getLength());
            String[] parts = response.split(";");

            MessageResponseDataModel responseMsg = new MessageResponseDataModel(parts[2],Integer.parseInt(parts[0]),Integer.parseInt(parts[1]));
            this.responses.add(responseMsg);
        }
    }

    public List<MessageResponseDataModel> getResponses() {
        return this.responses;
    }

    public void removeProcessedResponses(MessageResponseDataModel ele) {
        this.responses.remove(ele);
    }

}
