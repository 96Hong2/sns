package com.eunhong.sns.consumer;

import com.eunhong.sns.event.AlarmEvent;
import com.eunhong.sns.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmConsumer {

    private final AlarmService alarmService;

    // Alarm 이벤트를 컨슘하는 메소드
    // KafkaListener로 어떤 토픽에 대한 이벤트(메시지)를 받는지 명시
    @KafkaListener(topics = "${spring.kafka.topic.alarm}")
    public void consumeAlarm(AlarmEvent event, Acknowledgment ack) {
        log.info("Consume the event {}", event);
        alarmService.send(event.getAlarmType(), event.getArgs(), event.getReceiveUserId());
        ack.acknowledge(); // 완료 처리를 위해 에크 날림
    }
}
