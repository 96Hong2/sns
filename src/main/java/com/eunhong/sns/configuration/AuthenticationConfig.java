package com.eunhong.sns.configuration;

import com.eunhong.sns.configuration.filter.JwtTokenFilter;
import com.eunhong.sns.exception.CustomAuthenticationEntryPoint;
import com.eunhong.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    @Value("${jwt.secret-key}")
    private String key;

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
                .and()
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class)
                // 시큐리티에서 어떤 exception 받았을 때 어떤 EntryPoint로 가게 설정하는 것
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ;

        super.configure(http);
    }
}
