package Frontend;

import Util.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpRecieveFromReplicaManager {
    private DatagramSocket socket;
    private int port;

    public UdpRecieveFromReplicaManager(int port) {
        // Create a DatagramSocket to listen for requests on the specified port
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        this.port = port;
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
            String request = new String(packet.getData(), 0, packet.getLength());

            // Process the request (in this example, just print it to the console)
            System.out.println("Received request: " + request);
        }
    }

    public static void main(String[] args) throws IOException {
        // Set up the UDPServer to listen for requests on port 5000
        UdpRecieveFromReplicaManager server = new UdpRecieveFromReplicaManager(Constants.FE_Port);

        // Start listening for requests
        server.listen();
    }

}
