package org.example.searadar.mr231_3.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class Mr231_3ConverterTest {
    Mr231_3Converter mr231_3Converter = new Mr231_3Converter();
    TrackedTargetMessage expectedTTM;
    RadarSystemDataMessage expectedRSD;

    @BeforeEach
    void init() {
        expectedTTM = new TrackedTargetMessage();
        expectedTTM.setTargetNumber(6);
        expectedTTM.setDistance(28.71);
        expectedTTM.setBearing(341.1);
        expectedTTM.setCourse(24.5);
        expectedTTM.setSpeed(57.6);
        expectedTTM.setType(TargetType.UNKNOWN);
        expectedTTM.setStatus(TargetStatus.LOST);
        expectedTTM.setIff(IFF.FRIEND);

        expectedRSD = new RadarSystemDataMessage();
        expectedRSD.setInitialDistance(12.0);
        expectedRSD.setInitialBearing(28.71);
        expectedRSD.setMovingCircleOfDistance(341.1);
        expectedRSD.setBearing(23.0);
        expectedRSD.setDistanceFromShip(4.1);
        expectedRSD.setBearing2(21.0);
        expectedRSD.setDistanceScale(12.0);
        expectedRSD.setDistanceUnit("K");
        expectedRSD.setDisplayOrientation("C");
        expectedRSD.setWorkingMode("S");
    }

    @Test
    void convert_TTMCorrectData_ReturnTTM() throws Exception {
        String mr231_3_TTM = "$RATTM,6,28.71,341.1,T,57.6,024.5,T,0.4,4.1,N,b,L,,457362,А*42";
        List<SearadarStationMessage> actualList = mr231_3Converter.convert(mr231_3_TTM);
        TrackedTargetMessage actualTTM = (TrackedTargetMessage) actualList.get(0);

        assertEquals(expectedTTM.getTargetNumber(), actualTTM.getTargetNumber());
        assertEquals(expectedTTM.getDistance(), actualTTM.getDistance());
        assertEquals(expectedTTM.getBearing(), actualTTM.getBearing());
        assertEquals(expectedTTM.getCourse(), actualTTM.getCourse());
        assertEquals(expectedTTM.getSpeed(), actualTTM.getSpeed());
        assertEquals(expectedTTM.getType(), actualTTM.getType());
        assertEquals(expectedTTM.getStatus(), actualTTM.getStatus());
        assertEquals(expectedTTM.getIff(), actualTTM.getIff());

        assertEquals(1, actualList.size());
    }

    @Test
    void convert_RSDCorrectData_ReturnRSD() throws Exception {
        String mr231_3_RSD = "$RARSD,12,28.71,341.1,23,,,,,4.1,21,12,K,C,S*42";
        List<SearadarStationMessage> actualList = mr231_3Converter.convert(mr231_3_RSD);
        RadarSystemDataMessage actualRSD = (RadarSystemDataMessage) actualList.get(0);

        assertEquals(expectedRSD.getInitialDistance(), actualRSD.getInitialDistance());
        assertEquals(expectedRSD.getInitialBearing(), actualRSD.getInitialBearing());
        assertEquals(expectedRSD.getMovingCircleOfDistance(), actualRSD.getMovingCircleOfDistance());
        assertEquals(expectedRSD.getBearing(), actualRSD.getBearing());
        assertEquals(expectedRSD.getDistanceFromShip(), actualRSD.getDistanceFromShip());
        assertEquals(expectedRSD.getBearing2(), actualRSD.getBearing2());
        assertEquals(expectedRSD.getDistanceScale(), actualRSD.getDistanceScale());
        assertEquals(expectedRSD.getDistanceUnit(), actualRSD.getDistanceUnit());
        assertEquals(expectedRSD.getDisplayOrientation(), actualRSD.getDisplayOrientation());
        assertEquals(expectedRSD.getWorkingMode(), actualRSD.getWorkingMode());

        assertEquals(1, actualList.size());
    }

    @Test
    void convert_IncorrectTTMSise_ReturnInvalidMessage() throws Exception {
        String mr231_3_TTM = "$RATTM,6,28.71,341.1,T,57.6,024.5,T,0.4,4.1,N,b,L,,457362,1,А*42";
        List<SearadarStationMessage> actualList = mr231_3Converter.convert(mr231_3_TTM);
        InvalidMessage invalidMessage = (InvalidMessage) actualList.get(0);
        assertEquals(1, actualList.size());
        assertTrue(invalidMessage.getInfoMsg().contains("Размер неправильный для TTM"));
    }

    @Test
    void convert_IncorrectRSDSise_ReturnInvalidMessage() throws Exception {
        String mr231_3_RSD = "$RARSD,12,28.71,341.1,23,,,,,4.1,21,K,C,S*42";
        List<SearadarStationMessage> actualList = mr231_3Converter.convert(mr231_3_RSD);
        InvalidMessage invalidMessage = (InvalidMessage) actualList.get(0);
        assertEquals(1, actualList.size());
        assertTrue(invalidMessage.getInfoMsg().contains("Размер неправильный для RSD"));
    }

    @Test
    void convert_NoRightMsgType_ReturnEmptyArray() throws Exception {
        String mr231_3_IncorrectType = "$RAFFF,12,28.71,341.1,23,,,,,4.1,21,K,C,S*42";
        List<SearadarStationMessage> actualList = mr231_3Converter.convert(mr231_3_IncorrectType);
        assertTrue(actualList.isEmpty());
    }

    @Test
    void readFields_CorrectData_ReturnNothing() throws Exception {
        //в строке есть *, длина строки не меньше 3
        Method readFieldsMethod = Mr231_3Converter.class.getDeclaredMethod("readFields", String.class);
        readFieldsMethod.setAccessible(true);
        readFieldsMethod.invoke(mr231_3Converter, "123345*");
        Field msgTypeField = mr231_3Converter.getClass().getDeclaredField("msgType");
        msgTypeField.setAccessible(true);
        String msgType = (String) msgTypeField.get(mr231_3Converter);
        Field fieldsField = mr231_3Converter.getClass().getDeclaredField("fields");
        fieldsField.setAccessible(true);
        String[] fields = (String[]) fieldsField.get(mr231_3Converter);
        assertEquals("345", msgType);
        assertArrayEquals(new String[]{"345"}, fields);

    }

    @Test
    void readFields_IncorrectData_ExceptionThrown() throws Exception {
        // проверка на то, есть в строке минимальное кол-во символов,
        // и где верно ли расположена *
        Method readFieldsMethod = Mr231_3Converter.class.getDeclaredMethod("readFields", String.class);
        readFieldsMethod.setAccessible(true);
        Exception thrown = assertThrows(Exception.class, () -> readFieldsMethod.invoke(mr231_3Converter, "1*"));
        assertTrue(thrown.getCause().toString().contains("Ошибка в введенной строке:"));
        thrown = assertThrows(Exception.class, () -> readFieldsMethod.invoke(mr231_3Converter, "11324345346"));
        assertTrue(thrown.getCause().toString().contains("Ошибка в введенной строке:"));
    }

    @Test
    void getTTM_CorrectData_ReturnTrackedTargetMessage() throws Exception {
        String[] fields = new String[]{"TTM", "6", "28.71", "341.1", "T", "57.6", "024.5", "T", "0.4", "4.1", "N", "b", "L", "", "457362", "А"};
        Method getTTMMethod = Mr231_3Converter.class.getDeclaredMethod("getTTM", null);
        getTTMMethod.setAccessible(true);

        Field fieldsField = mr231_3Converter.getClass().getDeclaredField("fields");
        fieldsField.setAccessible(true);
        fieldsField.set(mr231_3Converter, fields);
        TrackedTargetMessage actualTTM = (TrackedTargetMessage) getTTMMethod.invoke(mr231_3Converter, null);

        assertEquals(expectedTTM.getTargetNumber(), actualTTM.getTargetNumber());
        assertEquals(expectedTTM.getDistance(), actualTTM.getDistance());
        assertEquals(expectedTTM.getBearing(), actualTTM.getBearing());
        assertEquals(expectedTTM.getCourse(), actualTTM.getCourse());
        assertEquals(expectedTTM.getSpeed(), actualTTM.getSpeed());
        assertEquals(expectedTTM.getType(), actualTTM.getType());
        assertEquals(expectedTTM.getStatus(), actualTTM.getStatus());
        assertEquals(expectedTTM.getIff(), actualTTM.getIff());
    }

    @Test
    void getTTM_IncorrectData_ExceptionThrown() throws Exception {
        // exception while parsing
        String[] fields = new String[]{"TTM", "6", "28.71", "341.1", "T", "57.6", "024.5", "T", "0.4", "4.1", "N", "b", "L", "", "457362", "А"};
        Method getTTMMethod = Mr231_3Converter.class.getDeclaredMethod("getTTM", null);
        getTTMMethod.setAccessible(true);
        Field fieldsField = mr231_3Converter.getClass().getDeclaredField("fields");
        fieldsField.setAccessible(true);

        fields[1]="rre";
        fieldsField.set(mr231_3Converter, fields);
        Exception thrown = assertThrows(Exception.class, () -> getTTMMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("TTM Ошибка во время преобразования введения данных"));


        fields = new String[]{"TTM", "6", "28.71", "341.1", "T", "57.6", "024.5", "T", "0.4", "4.1", "N", "b", "L", "", "457362", "А"};
        fields[2]="rre";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getTTMMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("TTM Ошибка во время преобразования введения данных"));

        fields = new String[]{"TTM", "6", "28.71", "341.1", "T", "57.6", "024.5", "T", "0.4", "4.1", "N", "b", "L", "", "457362", "А"};
        fields[3]="rre";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getTTMMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("TTM Ошибка во время преобразования введения данных"));

        fields = new String[]{"TTM", "6", "28.71", "341.1", "T", "57.6", "024.5", "T", "0.4", "4.1", "N", "b", "L", "", "457362", "А"};
        fields[5]="rre";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getTTMMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("TTM Ошибка во время преобразования введения данных"));

        fields = new String[]{"TTM", "6", "28.71", "341.1", "T", "57.6", "024.5", "T", "0.4", "4.1", "N", "b", "L", "", "457362", "А"};
        fields[6]="rre";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getTTMMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("TTM Ошибка во время преобразования введения данных"));

    }

    @Test
    void getRSD_CorrectData_ReturnRadarSystemDataMessage() throws Exception {
        String[] fields = new String[]{"RSD", "12", "28.71", "341.1", "23", "", "", "", "", "4.1", "21", "12", "K", "C", "S"};
        Method getRSDMethod = Mr231_3Converter.class.getDeclaredMethod("getRSD", null);
        getRSDMethod.setAccessible(true);

        Field fieldsField = mr231_3Converter.getClass().getDeclaredField("fields");
        fieldsField.setAccessible(true);
        fieldsField.set(mr231_3Converter, fields);
        RadarSystemDataMessage actualRSD = (RadarSystemDataMessage) getRSDMethod.invoke(mr231_3Converter, null);
        assertEquals(expectedRSD.getInitialDistance(), actualRSD.getInitialDistance());
        assertEquals(expectedRSD.getInitialBearing(), actualRSD.getInitialBearing());
        assertEquals(expectedRSD.getMovingCircleOfDistance(), actualRSD.getMovingCircleOfDistance());
        assertEquals(expectedRSD.getBearing(), actualRSD.getBearing());
        assertEquals(expectedRSD.getDistanceFromShip(), actualRSD.getDistanceFromShip());
        assertEquals(expectedRSD.getBearing2(), actualRSD.getBearing2());
        assertEquals(expectedRSD.getDistanceScale(), actualRSD.getDistanceScale());
        assertEquals(expectedRSD.getDistanceUnit(), actualRSD.getDistanceUnit());
        assertEquals(expectedRSD.getDisplayOrientation(), actualRSD.getDisplayOrientation());
        assertEquals(expectedRSD.getWorkingMode(), actualRSD.getWorkingMode());
    }

    @Test
    void getRSD_IncorrectData_ExceptionThrown() throws Exception {
        String[] fields = new String[]{"RSD", "12", "28.71", "341.1", "23", "", "", "", "", "4.1", "21", "12", "K", "C", "S"};
        Method getRSDMethod = Mr231_3Converter.class.getDeclaredMethod("getRSD", null);
        getRSDMethod.setAccessible(true);
        Field fieldsField = mr231_3Converter.getClass().getDeclaredField("fields");
        fieldsField.setAccessible(true);

        fields[1]="qw";
        fieldsField.set(mr231_3Converter, fields);
        Exception thrown = assertThrows(Exception.class, () -> getRSDMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("RSD Ошибка во время преобразования введенных данных"));

        fields = new String[]{"RSD", "12", "28.71", "341.1", "23", "", "", "", "", "4.1", "21", "12", "K", "C", "S"};
        fields[2]="hjh";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getRSDMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("RSD Ошибка во время преобразования введенных данных"));

        fields = new String[]{"RSD", "12", "28.71", "341.1", "23", "", "", "", "", "4.1", "21", "12", "K", "C", "S"};
        fields[3]="hjh";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getRSDMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("RSD Ошибка во время преобразования введенных данных"));

        fields = new String[]{"RSD", "12", "28.71", "341.1", "23", "", "", "", "", "4.1", "21", "12", "K", "C", "S"};
        fields[4]="hjh";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getRSDMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("RSD Ошибка во время преобразования введенных данных"));

        fields = new String[]{"RSD", "12", "28.71", "341.1", "23", "", "", "", "", "4.1", "21", "12", "K", "C", "S"};
        fields[9]="hjh";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getRSDMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("RSD Ошибка во время преобразования введенных данных"));

        fields = new String[]{"RSD", "12", "28.71", "341.1", "23", "", "", "", "", "4.1", "21", "12", "K", "C", "S"};
        fields[10]="hjh";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getRSDMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("RSD Ошибка во время преобразования введенных данных"));

        fields = new String[]{"RSD", "12", "28.71", "341.1", "23", "", "", "", "", "4.1", "21", "12", "K", "C", "S"};
        fields[11]="hjh";
        fieldsField.set(mr231_3Converter, fields);
        thrown = assertThrows(Exception.class, () -> getRSDMethod.invoke(mr231_3Converter, null));
        assertTrue(thrown.getCause().toString().contains("RSD Ошибка во время преобразования введенных данных"));
    }
}