package Util;

import Replicas.Replica1.Shared.data.MovieState;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class sortRmResponses {
    public static List<MessageResponseDataModel> sortRm(List<MessageResponseDataModel> obj) {
        Collections.sort(obj,new Comparator<MessageResponseDataModel>() {
            @Override
            public int compare(MessageResponseDataModel o1, MessageResponseDataModel o2) {
                return o1.replicaManager - (o2.replicaManager);
            }
        });
        return obj;
    }
}
