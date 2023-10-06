package com.eunhong.sns.controller.response;

import com.eunhong.sns.model.Alarm;
import com.eunhong.sns.model.AlarmArgs;
import com.eunhong.sns.model.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
public class AlarmResponse {

    private Integer id;
    private AlarmType alarmType;
    private AlarmArgs args;
    private String text;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static AlarmResponse fromAlarm(Alarm alarm) {
        return new AlarmResponse(
                alarm.getId(),
                alarm.getAlarmType(),
                alarm.getArgs(),
                alarm.getAlarmType().getAlarmText(),
                alarm.getRegisteredAt(),
                alarm.getUpdatedAt(),
                alarm.getDeletedAt()
        );
    }
}
