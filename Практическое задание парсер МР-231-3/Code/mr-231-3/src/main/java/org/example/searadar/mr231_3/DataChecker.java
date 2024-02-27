package org.example.searadar.mr231_3;

import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;

import java.util.List;

/**
 *  Интерфейс для проверки правильности данных
 *
 */
public interface DataChecker {
    /**
     * Проверить данные
     *
     * <p>
     *     Проверяет данные на соответствие правильным значениям для данного
     *     типа сообщений (проверка на отсутствие за пределы допустимых значений).
     *     Если ошибок не найдено, то возвращается пустой список.
     * @param ssm -- сообщение класса SearadarStationMessage
     * @return List<InvalidMessage> -- список найденных ошибок
     * @see InvalidMessage
     */
    List<InvalidMessage> checkData(SearadarStationMessage ssm);

    /**
     * Добавить сообщение об ошибке в list
     *
     * @param list -- список с ошибками
     * @param msgInfo -- сообщение, которое нужно добавить в list
     * @return List<InvalidMessage> -- список с ошибками
     * @see InvalidMessage
     */ // нужно объяснить необходимость этого
    static List<InvalidMessage> addMsgInfo(List<InvalidMessage> list, String msgInfo){
        InvalidMessage invalidMessage = new InvalidMessage();
        invalidMessage.setInfoMsg(msgInfo);
        list.add(invalidMessage);
        return list;
    }
}
