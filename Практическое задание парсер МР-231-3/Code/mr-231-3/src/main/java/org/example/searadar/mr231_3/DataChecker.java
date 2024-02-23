package org.example.searadar.mr231_3;

import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;

import java.util.List;

public interface DataChecker {
    List<InvalidMessage> checkData(SearadarStationMessage ssm);

    // нужно объяснить необходимость этого
    static List<InvalidMessage> addMsgInfo(List<InvalidMessage> list, String msgInfo){
        InvalidMessage invalidMessage = new InvalidMessage();
        invalidMessage.setInfoMsg(msgInfo);
        list.add(invalidMessage);
        return list;
    }
}
