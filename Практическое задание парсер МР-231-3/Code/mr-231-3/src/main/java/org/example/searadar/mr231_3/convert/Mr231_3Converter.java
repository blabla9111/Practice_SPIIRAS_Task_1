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

/**
 * Класс для парсинга сообщений из формата протокола mr231-3
 */
public class Mr231_3Converter implements SearadarExchangeConverter {
    /**
     * массив данных из полученного сообщения
     */
    private String[] fields;
    private String msgType;

    @Override
    public List<SearadarStationMessage> convert(Exchange exchange) throws Exception {
        return convert(exchange.getIn().getBody(String.class));
    }

    /**
     * Перевод из формата mr231-3 в SearadarStationMessage
     *
     * <p>
     * Распознает сообщения 2-х типов TTM -- TrackedTargetMessage
     * RSD -- RadarSystemDataMessage. Если пришло сообщение другого типа,
     * то вернется пустой список List<SearadarStationMessage>
     *
     * @param message -- сообщение в формате mr231_3
     * @return список прочитанных сообщений приведенных в тип SearadarStationMessage
     * @throws Exception -- выбрасывает ошибку если формат данных не соответствует протоколу mr231_3
     * @see SearadarStationMessage
     */
    public List<SearadarStationMessage> convert(String message) throws Exception {

        List<SearadarStationMessage> msgList = new ArrayList<>();
        DataChecker checker;
        List<InvalidMessage> invalidMessages = new ArrayList<>();
        readFields(message);

        switch (msgType) {

            case "TTM":
                // Проверка на соответсвие кол-ва данных в fields для типа TTM
                if (fields.length == 16) {
                    TrackedTargetMessage ttm = getTTM();
                    checker = new DataCheckerTTM();
                    // Проверка на наличие ошибок в значениях для TTM
                    invalidMessages = checker.checkData(ttm);
                    if (!invalidMessages.isEmpty()) {
                        msgList.addAll(invalidMessages);
                    } else {
                        msgList.add(ttm);
                    }
                } else {
                    DataChecker.addMsgInfo(invalidMessages, "Размер неправильный для TTM " + fields.length);
                    msgList.addAll(invalidMessages);
                }

                break;

            case "RSD": {
                // Проверка на соответсвие кол-ва данных в fields для типа RSD
                if (fields.length == 15) {
                    RadarSystemDataMessage rsd = getRSD();
                    // Проверка на наличие ошибок в значениях для RSD
                    checker = new DataCheckerRSD();
                    invalidMessages = checker.checkData(rsd);
                    if (!invalidMessages.isEmpty()) {
                        msgList.addAll(invalidMessages);
                    } else {
                        msgList.add(rsd);
                    }
                } else {
                    DataChecker.addMsgInfo(invalidMessages, "Размер неправильный для RSD " + fields.length);
                    msgList.addAll(invalidMessages);
                }

                break;
            }

        }

        return msgList;
    }

    /**
     * получает массив данных fileds из сообщения
     *
     * <p>
     * Если формат сообщения соответствует mr231_3, то в поля класса
     * Mr231_3Converter fields и msgType будут положены соответсвующие значения
     * из сообщения msg
     *
     * @param msg -- сообщение в формате mr231_3
     * @throws Exception -- выбрасывает ошибку в случае, если формат не подходит под mr231_3
     */
    private void readFields(String msg) throws Exception {
        try {
            String temp = msg.substring(3, msg.indexOf("*")).trim();

            fields = temp.split(Pattern.quote(","));
            msgType = fields[0];
        } catch (Exception e) {
            throw new Exception("Ошибка в введенной строке:\n" + e.getMessage());
        }

    }

    /**
     * Отдает сообщение типа RadarSystemDataMessage
     *
     * @return {@link RadarSystemDataMessage}
     * @throws Exception -- выбрасывает исключение если во время парсинга
     *                   какого-либо значения из fields произошла ошибка
     * @see RadarSystemDataMessage
     */
    private RadarSystemDataMessage getRSD() throws Exception {
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
        } catch (Exception e) {
            throw new Exception("RSD Ошибка во время преобразования введенных данных\n" + e.getMessage());
        }

        return rsd;
    }

    /**
     * Отдает сообщение типа TrackedTargetMessage
     *
     * @return {@link TrackedTargetMessage}
     * @throws Exception -- выбрасывает исключение если во время парсинга
     *                   какого-либо значения из fields произошла ошибка
     * @see TrackedTargetMessage
     */
    private TrackedTargetMessage getTTM() throws Exception {
        TrackedTargetMessage ttm = new TrackedTargetMessage();
        Long msgRecTimeMillis = System.currentTimeMillis();

        ttm.setMsgTime(msgRecTimeMillis);
        TargetStatus status = TargetStatus.UNRELIABLE_DATA;
        IFF iff = IFF.UNKNOWN;
        TargetType type = TargetType.UNKNOWN;

        switch (fields[11]) {
            case "b":
                iff = IFF.FRIEND;
                break;

            case "p":
                iff = IFF.FOE;
                break;

            case "d":
                iff = IFF.UNKNOWN;
                break;
        }

        switch (fields[12]) {
            case "L":
                status = TargetStatus.LOST;
                break;

            case "Q":
                status = TargetStatus.UNRELIABLE_DATA;
                break;

            case "T":
                status = TargetStatus.TRACKED;
                break;
        }

        ttm.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        try {
            ttm.setTargetNumber(Integer.parseInt(fields[1]));
            ttm.setDistance(Double.parseDouble(fields[2]));
            ttm.setBearing(Double.parseDouble(fields[3]));

            ttm.setSpeed(Double.parseDouble(fields[5]));
            ttm.setCourse(Double.parseDouble(fields[6]));
        } catch (Exception e) {
            throw new Exception("TTM Ошибка во время преобразования введения данных\n" + e.getMessage());
        }
        ttm.setStatus(status);
        ttm.setIff(iff);

        ttm.setType(type);
        return ttm;
    }


}
