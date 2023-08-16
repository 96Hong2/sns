package com.eunhong.sns.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                // 회원가입이나 로그인 시에는 인증 없이 어떤 유저든지 허용하도록 함, 버전정보는 *로 설정
                .antMatchers("/api/*/users/join", "/api/*/users/login").permitAll()
                .antMatchers("api/**").authenticated() // 그 외는 항상 인증필요
                .and()
                // 세션은 따로 관리 안하는 방향
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
        // TODO
        // 시큐리티에서 어떤 exception 받았을 때 어떤 EntryPoint로 가게 설정하는 것, 일단 나중에 구현
        // .exceptionHandling()
        // .authenticationEntryPoint()

        super.configure(http);
    }
}
