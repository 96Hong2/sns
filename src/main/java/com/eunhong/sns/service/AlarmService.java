package com.eunhong.sns.service;

import com.eunhong.sns.exception.ErrorCode;
import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {
    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // sseEmitter 타임아웃
    private final static String ALARM_NAME = "alarm"; // 알림 이벤트 이름 (index.js의 이벤트리스너에 설정된 이벤트명)
    private final EmitterRepository emitterRepository;

    // 알림 발생 시 브라우저에 알림 이벤트를 보내기 위한 메소드
    public void send(Integer alarmId, Integer userId) {
        emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(alarmId.toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(userId);
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded."));
    }

    public SseEmitter connectAlarm(Integer userId) {

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter); // 커넥트 시 sseEmitter 저장

        // 이벤트 완료 시 저장된 sseEmitter를 지워줌
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        // 이벤트 타임아웃 시 저장된 sseEmitter를 지워줌
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            sseEmitter.send(SseEmitter.event().id("").name(ALARM_NAME).data("connection completed."));
        } catch (IOException e) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }
}
