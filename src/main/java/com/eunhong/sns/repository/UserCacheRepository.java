package com.eunhong.sns.repository;

import com.eunhong.sns.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserCacheRepository { //Redis에 유저를 캐싱하고 가져오는 클래스

    private final RedisTemplate<String, User> userRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3); // 3일 캐시 타임아웃

    public void setUser(User user) { // User를 캐시에 세팅
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {} , {}", key, user);
        userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL); // key, value
    }

    public Optional<User> getUser(String userName) {
        String key = getKey(userName);
        User user = userRedisTemplate.opsForValue().get(key);
        log.info("Get data from Redis {} , {}", key, user);
        return Optional.ofNullable(user);
    }

    private String getKey(String userName) { // JwtTokenFilter에서 사용하는 키명 userName을 사용
        return "USER:" + userName; // 다양한 객체의 캐싱을 하게 될 수 있으므로 prefix를 붙여주는 것이 좋다.
    }
}
