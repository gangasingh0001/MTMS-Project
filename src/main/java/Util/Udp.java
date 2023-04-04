package Util;

import java.io.IOException;
import java.net.*;

public class Udp {
//    private static String FE_listenForUDPResponses() {
//        DatagramSocket aSocket = null;
//        try {
//            InetAddress desiredAddress = InetAddress.getByName(FE_IP_Address);
//            aSocket = new DatagramSocket(FE_PORT, desiredAddress);
//            byte[] buffer = new byte[1000];
//            System.out.println("FE Server Started on " + desiredAddress + ":" + FE_PORT + "............");
//
//            while (true) {
//                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
//                aSocket.receive(response);
//                String sentence = new String(response.getData(), 0,
//                        response.getLength()).trim();
//                System.out.println("FE:Response received from Rm>>>" + sentence);
//                //RmResponse rmResponse = new RmResponse(sentence);
//
//                System.out.println("Adding response to FrontEndImplementation:");
//                //return rmResponse;
//            }
//
//        } catch (SocketException e) {
//            System.out.println("Socket: " + e.getMessage());
//        } catch (IOException e) {
//            System.out.println("IO: " + e.getMessage());
//        } catch (SocketException e) {
//            throw new RuntimeException(e);
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//    }

//    private static int sendUnicastToSequencer(MyRequest requestFromClient) {
//        DatagramSocket aSocket = null;
//        String dataFromClient = requestFromClient.toString();
//        System.out.println("FE:sendUnicastToSequencer>>>" + dataFromClient);
//        int sequenceID = 0;
//        try {
//            aSocket = new DatagramSocket(FE_SQ_PORT);
//            byte[] message = dataFromClient.getBytes();
//            InetAddress aHost = InetAddress.getByName(Constants.Sequencer_IPAddress);
//            DatagramPacket requestToSequencer = new DatagramPacket(message, dataFromClient.length(), aHost, Constants.Sequencer_Port);
//
//            aSocket.send(requestToSequencer);
//
//            aSocket.setSoTimeout(1000);
//            // Set up an UPD packet for recieving
//            byte[] buffer = new byte[1000];
//            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
//            // Try to receive the response from the ping
//            aSocket.receive(response);
//            String sentence = new String(response.getData(), 0,
//                    response.getLength());
//            System.out.println("FE:sendUnicastToSequencer/ResponseFromSequencer>>>" + sentence);
//            sequenceID = Integer.parseInt(sentence.trim());
//            System.out.println("FE:sendUnicastToSequencer/ResponseFromSequencer>>>SequenceID:" + sequenceID);
//        } catch (SocketException e) {
//            System.out.println("Failed: " + requestFromClient.noRequestSendError());
//            System.out.println("Socket: " + e.getMessage());
//        } catch (IOException e) {
//            System.out.println("Failed: " + requestFromClient.noRequestSendError());
//            e.printStackTrace();
//            System.out.println("IO: " + e.getMessage());
//        } finally {
//            if (aSocket != null)
//                aSocket.close();
//        }
//        return sequenceID;
//    }
}
