package org.example.searadar.mr231_3.Impl;

import org.example.searadar.mr231_3.DataChecker;
import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataCheckerRSD implements DataChecker {
    /**
     * @param ssm
     * @return
     */
    private static final Double[] DISTANCE_SCALE = {0.125, 0.25, 0.5, 1.5, 3.0, 6.0, 12.0, 24.0, 48.0, 96.0};

    @Override
    public List<InvalidMessage> checkData(SearadarStationMessage ssm) {
        return checkData((RadarSystemDataMessage) ssm);
    }

    public List<InvalidMessage> checkData(RadarSystemDataMessage rsd) {
        List<InvalidMessage> list = new ArrayList<>();
        if (rsd==null){
            DataChecker.addMsgInfo(list,"RSD = null");
            return list;
        }
        if (rsd.getBearing()<0){
            DataChecker.addMsgInfo(list,"Неверный пеленг: "+rsd.getBearing());
        }
        if (!Arrays.asList(DISTANCE_SCALE).contains(rsd.getDistanceScale())) {
            DataChecker.addMsgInfo(list, "Неверная шкала расстояний: "+rsd.getDistanceScale());
        }
        if (!rsd.getDistanceUnit().equals("K") && !rsd.getDistanceUnit().equals("N")){
            DataChecker.addMsgInfo(list,"Неверный формат единицы измерения расстояния (K/N): "+rsd.getDistanceUnit());
        }
        if(!rsd.getDisplayOrientation().equals("C") && !rsd.getDisplayOrientation().equals("H") && !rsd.getDisplayOrientation().equals("N")){
            DataChecker.addMsgInfo(list, "Неверный формат ориентации дисплея (C/H/N): "+rsd.getDisplayOrientation());
        }
        if (!rsd.getWorkingMode().equals("S") && !rsd.getWorkingMode().equals("P")){
            DataChecker.addMsgInfo(list, "Неверный формат режима работы НРЛС (S/P): "+rsd.getWorkingMode());
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@");
        return list;
    }
}
