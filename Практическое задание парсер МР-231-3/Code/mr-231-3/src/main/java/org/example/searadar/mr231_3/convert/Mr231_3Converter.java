package org.example.searadar.mr231_3.convert;

import org.apache.camel.Exchange;
import org.example.searadar.mr231_3.DataChecker;
import org.example.searadar.mr231_3.Impl.DataCheckerRSD;
import org.example.searadar.mr231_3.Impl.DataCheckerTTM;
import ru.oogis.searadar.api.convert.SearadarExchangeConverter;
import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Mr231_3Converter implements SearadarExchangeConverter {
    private String[] fields;
    private String msgType;

    @Override
    public List<SearadarStationMessage> convert(Exchange exchange) throws Exception {
        return convert(exchange.getIn().getBody(String.class));
    }

    public List<SearadarStationMessage> convert(String message){

        List<SearadarStationMessage> msgList = new ArrayList<>();
        DataChecker checker;
        List<InvalidMessage> invalidMessages;
        readFields(message);

        switch (msgType) {

            case "TTM" :
                // обавить проверку на кол-во полей для данного типа сообщений
                if (fields.length==16){
                    TrackedTargetMessage ttm = getTTM();
                    checker = new DataCheckerTTM();
                    invalidMessages = checker.checkData(ttm);
                    if (invalidMessages!=null){
                        msgList.addAll(invalidMessages);
//                        System.out.println(((InvalidMessage)msgList.get(0)).getInfoMsg());
                        msgList.stream().forEach(e->System.out.println(((InvalidMessage)e).getInfoMsg()));
                    }
                    else{
                        msgList.add(ttm);
                    }
                }
                else{
                    System.out.println("Размер неправильный для TTM "+fields.length);
                }

                break;

            case "RSD" : {
                // лучше сделать сначала проверку потом создавать rsd
                if(fields.length==15){
                    RadarSystemDataMessage rsd = getRSD();
                    checker = new DataCheckerRSD();
                    invalidMessages = checker.checkData(rsd);
                    if (invalidMessages!=null){
                        msgList.addAll(invalidMessages);
                        msgList.stream().forEach(e->System.out.println(((InvalidMessage)e).getInfoMsg()));
                    }
                    else{
                        msgList.add(rsd);
                    }
                }
                else{
                    System.out.println("Размер неправильный для RSD "+fields.length);
                }

                break;
            }

        }

        return msgList;
    }

    private void readFields(String msg) {
        // убираю $RA вначале и *ЧИСЛО в конце
        try{
            String temp = msg.substring( 3, msg.indexOf("*") ).trim();

            fields = temp.split(Pattern.quote(","));
            msgType = fields[0];
        }
        catch (Exception e){
            System.out.println("Ошибка в введенной строке:\n"+e.getMessage());
        }

    }

    private RadarSystemDataMessage getRSD() {
        RadarSystemDataMessage rsd = new RadarSystemDataMessage();

        rsd.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        try {
            rsd.setInitialDistance(Double.parseDouble(fields[1]));
            rsd.setInitialBearing(Double.parseDouble(fields[2]));
            rsd.setMovingCircleOfDistance(Double.parseDouble(fields[3]));
            rsd.setBearing(Double.parseDouble(fields[4]));
            rsd.setDistanceFromShip(Double.parseDouble(fields[9]));
            rsd.setBearing2(Double.parseDouble(fields[10]));
            rsd.setDistanceScale(Double.parseDouble(fields[11]));
            rsd.setDistanceUnit(fields[12]);
            rsd.setDisplayOrientation(fields[13]);
            rsd.setWorkingMode(fields[14]);
        }
        catch (Exception e){
            System.out.println("RSD Ошибка во время преобразования введения данных\n"+e.getMessage());
            return null;
        }


        return rsd;
    }

    private TrackedTargetMessage getTTM() {
        TrackedTargetMessage ttm = new TrackedTargetMessage();
        Long msgRecTimeMillis = System.currentTimeMillis();

        ttm.setMsgTime(msgRecTimeMillis);
        TargetStatus status = TargetStatus.UNRELIABLE_DATA;
        IFF iff = IFF.UNKNOWN;
        TargetType type = TargetType.UNKNOWN;

        switch (fields[11]) {
            case "b" : iff = IFF.FRIEND;
                break;

            case "p" : iff = IFF.FOE;
                break;

            case "d" : iff = IFF.UNKNOWN;
                break;
        }

        switch (fields[12]) {
            case "L" : status = TargetStatus.LOST;
                break;

            case "Q" : status = TargetStatus.UNRELIABLE_DATA;
                break;

            case "T" : status = TargetStatus.TRACKED;
                break;
        }

        ttm.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        try {
            ttm.setTargetNumber(Integer.parseInt(fields[1]));
            ttm.setDistance(Double.parseDouble(fields[2]));
            ttm.setBearing(Double.parseDouble(fields[3]));

            ttm.setSpeed(Double.parseDouble(fields[5]));
            ttm.setCourse(Double.parseDouble(fields[6]));
        }
        catch (Exception e){
            System.out.println("TTM Ошибка во время преобразования введения данных\n"+e.getMessage());
        }
        ttm.setStatus(status);
        ttm.setIff(iff);

        ttm.setType(type);

        return ttm;
    }


}
