package com.eunhong.sns.producer;

import com.eunhong.sns.event.AlarmEvent;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmProducer {

    private final KafkaTemplate<Integer, AlarmEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.alarm}")
    private String topic; // application.yaml에서 받아옴

    // 이벤트 보내기
    public void send(AlarmEvent event) {
        // 카프카 템플릿 send 파라미터 topic, key, value
        kafkaTemplate.send(topic, event.getReceiveUserId(), event);
        log.info("Send to kafka finished.");
    }
}
