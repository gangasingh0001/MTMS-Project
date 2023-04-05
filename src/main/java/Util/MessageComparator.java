package Util;

import java.util.Comparator;

public class MessageComparator implements Comparator<MessageDataModel> {
    @Override
    public int compare(MessageDataModel m1, MessageDataModel m2) {
        return m1.sequenceNumber - m2.sequenceNumber;
    }
}
