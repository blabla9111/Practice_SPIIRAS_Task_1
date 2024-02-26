package org.example.searadar.mr231_3.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataCheckerTTMTest {
    DataCheckerTTM dataCheckerTTM;
    TrackedTargetMessage ttm;

    @BeforeEach
    void init() {
        dataCheckerTTM = new DataCheckerTTM();
        ttm = new TrackedTargetMessage();
        ttm.setTargetNumber(6);
        ttm.setDistance(28.71);
        ttm.setBearing(341.1);
        ttm.setCourse(24.5);
        ttm.setSpeed(57.6);
        ttm.setType(TargetType.UNKNOWN);
        ttm.setStatus(TargetStatus.LOST);
        ttm.setIff(IFF.FRIEND);
    }

    @Test
    void checkData_CorrectData_ReturnEmptyList() {
        List<InvalidMessage> actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(0, actualList.size());
    }

    @Test
    void checkData_IncorrectTargetNumber_ReturnListInvalidMessages() {

        ttm.setTargetNumber(0);
        List<InvalidMessage> actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный номер цели"));

        ttm.setTargetNumber(-10);
        actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный номер цели"));

        ttm.setTargetNumber(52);
        actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный номер цели"));
    }

    @Test
    void checkData_IncorrectDistance_ReturnListInvalidMessages() {
        ttm.setDistance(-1.0);
        List<InvalidMessage> actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверное расстояние до цели"));

        ttm.setDistance(33.0);
        actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверное расстояние до цели"));
    }

    @Test
    void checkData_IncorrectBearing_ReturnListInvalidMessages() {
        ttm.setBearing(-1.0);
        List<InvalidMessage> actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный пеленг до цели"));

        ttm.setBearing(360.0);
        actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный пеленг до цели"));

        ttm.setBearing(400.0);
        actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный пеленг до цели"));
    }

    @Test
    void checkData_IncorrectSpeed_ReturnListInvalidMessages() {
        ttm.setSpeed(-1.0);
        List<InvalidMessage> actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверная скорость"));

        ttm.setSpeed(90.1);
        actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверная скорость"));

        ttm.setSpeed(100.0);
        actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверная скорость"));
    }

    @Test
    void checkData_IncorrectCourse_ReturnListInvalidMessages() {
        ttm.setCourse(-1.0);
        List<InvalidMessage> actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный курс цели"));

        ttm.setCourse(360.0);
        actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный курс цели"));

        ttm.setCourse(650.0);
        actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Неверный курс цели"));
    }

    @Test
    void checkData_IncorrectStatus_ReturnListInvalidMessages() {
        ttm.setStatus(null);
        List<InvalidMessage> actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Отсутствует статус цели"));
    }

    @Test
    void checkData_IncorrectIff_ReturnListInvalidMessages() {
        ttm.setIff(null);
        List<InvalidMessage> actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Отсутствует признак опознавания цели"));
    }

    @Test
    void checkData_IncorrectType_ReturnListInvalidMessages() {
        ttm.setType(null);
        List<InvalidMessage> actualList = dataCheckerTTM.checkData(ttm);
        assertEquals(1, actualList.size());
        InvalidMessage invalidMessage = actualList.get(0);
        assertTrue(invalidMessage.getInfoMsg().contains("Отсутствует тип цели"));
    }
}