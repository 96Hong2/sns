package com.eunhong.sns.configuration;

import com.eunhong.sns.model.User;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfiguration {

    // Redis 서버 설정을 yaml파일에 빼면, RedisProperties를 얻게 된다.
    private final RedisProperties redisProperties;

    // Redis 서버의 연결 정보를 가지는 ConnectionFactory 빈 생성
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisURI redisURI = RedisURI.create(redisProperties.getUrl()); // redisProperties에서 url를 가져와 넣어줌
        org.springframework.data.redis.connection.RedisConfiguration configuration =
                LettuceConnectionFactory.createRedisConfiguration(redisURI);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet(); // initializing
        return factory;
    }

    // Redis Template 빈 생성 : Redis의 다양한 커맨드를 쉽게 사용할 수 있도록 도와주는 클래스
    @Bean
    public RedisTemplate<String, User> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory); // Redis 서버 연결 정보를 가짐
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // key 직렬화
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class)); // value 직렬화 > User는 우리가 만든 커스텀 Object이다.
        return redisTemplate;
    }
}
