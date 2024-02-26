package org.example.searadar.mr231_3.Impl;

import org.example.searadar.mr231_3.DataChecker;
import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;

import java.util.ArrayList;
import java.util.List;

public class DataCheckerTTM implements DataChecker {
    @Override
    public List<InvalidMessage> checkData(SearadarStationMessage ssm) {
        return checkData((TrackedTargetMessage) ssm);
    }

    public List<InvalidMessage> checkData(TrackedTargetMessage ttm) {
        List<InvalidMessage> list = new ArrayList<>();
        if ( ttm.getTargetNumber()<1 || ttm.getTargetNumber()>50){
            DataChecker.addMsgInfo(list,"Неверный номер цели: "+ttm.getTargetNumber());
        }
        if (ttm.getDistance()<0 || ttm.getDistance() >32){
            DataChecker.addMsgInfo(list,"Неверное расстояние до цели: "+ttm.getDistance());
        }
        if (ttm.getBearing()<0 || ttm.getBearing()>=360){
            DataChecker.addMsgInfo(list,"Неверный пеленг до цели: "+ttm.getBearing());
        }
        if (ttm.getSpeed()<0 || ttm.getSpeed()>90){
            DataChecker.addMsgInfo(list,"Неверная скорость: "+ttm.getSpeed());
        }
        if(ttm.getCourse()<0 || ttm.getCourse()>=360){
            DataChecker.addMsgInfo(list,"Неверный курс цели: "+ttm.getCourse());
        }
        if (ttm.getStatus()==null){
            DataChecker.addMsgInfo(list,"Отсутствует статус цели");
        }
        if (ttm.getIff()==null){
            DataChecker.addMsgInfo(list,"Отсутствует признак опознавания цели");
        }
        if (ttm.getType()==null){
            DataChecker.addMsgInfo(list,"Отсутствует тип цели");
        }
        return list;
    }
}
