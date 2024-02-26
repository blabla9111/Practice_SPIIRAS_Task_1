package org.example.searadar.mr231_3.Impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataCheckerRSDTest {
    DataCheckerRSD dataCheckerRSD;

    RadarSystemDataMessage rsd;

    @BeforeEach
    void init() {
        dataCheckerRSD = new DataCheckerRSD();
        rsd = new RadarSystemDataMessage();
        rsd.setInitialDistance(12.0);
        rsd.setInitialBearing(28.71);
        rsd.setMovingCircleOfDistance(341.1);
        rsd.setBearing(23.0);
        rsd.setDistanceFromShip(4.1);
        rsd.setBearing2(21.0);
        rsd.setDistanceScale(12.0);
        rsd.setDistanceUnit("K");
        rsd.setDisplayOrientation("C");
        rsd.setWorkingMode("S");
    }

    @Test
    void check_DISTANCE_SCALE_Field() throws Exception {

        Field DISTANCE_SCALE_Field = DataCheckerRSD.class.getDeclaredField("DISTANCE_SCALE");
        Double[] excepted_DISTANCE_SCALE = {0.125, 0.25, 0.5, 1.5, 3.0, 6.0, 12.0, 24.0, 48.0, 96.0};
        DISTANCE_SCALE_Field.setAccessible(true);
        Double[] actual_DISTANCE_SCALE = (Double[]) DISTANCE_SCALE_Field.get(dataCheckerRSD);
        assertArrayEquals(excepted_DISTANCE_SCALE, actual_DISTANCE_SCALE);
    }

    @Test
    void checkData_CorrectData_ReturnEmptyList() {
        List<InvalidMessage> actualList = dataCheckerRSD.checkData(rsd);
        assertEquals(0, actualList.size());
    }

    @Test
    void checkData_IncorrectBearing_ReturnListInvalidMessages() {
        rsd.setBearing(-23.0);
        List<InvalidMessage> actualList = dataCheckerRSD.checkData(rsd);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный пеленг"));
    }

    @Test
    void checkData_IncorrectDistanceScale_ReturnListInvalidMessages() {
        rsd.setDistanceScale(120.0);
        List<InvalidMessage> actualList = dataCheckerRSD.checkData(rsd);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверная шкала расстояний"));
    }

    @Test
    void checkData_IncorrectDistanceUnit_ReturnListInvalidMessages() {
        rsd.setDistanceUnit("L");
        List<InvalidMessage> actualList = dataCheckerRSD.checkData(rsd);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный формат единицы измерения расстояния (K/N)"));
    }

    @Test
    void checkData_IncorrectDisplayOrientation_ReturnListInvalidMessages() {
        rsd.setDisplayOrientation("M");
        List<InvalidMessage> actualList = dataCheckerRSD.checkData(rsd);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный формат ориентации дисплея (C/H/N)"));
    }

    @Test
    void checkData_IncorrectWorkingMode_ReturnListInvalidMessages() {
        rsd.setWorkingMode("M");
        List<InvalidMessage> actualList = dataCheckerRSD.checkData(rsd);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный формат режима работы НРЛС (S/P)"));
    }
}