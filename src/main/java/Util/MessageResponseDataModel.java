package Util;

public class MessageResponseDataModel {
    public String response = null;
    public int sequenceNumber = 0;
    public int replicaManager = 0;

    public MessageResponseDataModel (String response, int sequenceNumber, int replicaManager) {
        this.response = response;
        this.sequenceNumber = sequenceNumber;
        this.replicaManager = replicaManager;
    }
}
