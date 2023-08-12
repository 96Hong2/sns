package com.eunhong.sns.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        // BCryptPasswordEncoder는 spring-boot-starter-security 안에 있음
        return new BCryptPasswordEncoder();
    }
}
