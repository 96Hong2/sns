package com.eunhong.sns.configuration.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter { // 매 요청 때마다 필터 씌움
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // get header
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION); // header의 AUTHORIZATION에 토큰 값 넣어줌
        if(header == null || !header.startsWith("Bearer ")) {
            // Bearer 토큰을 사용할 것, 토큰 헤더가 'Bearer'로 시작해야 함
            log.error("Error occurs while getting header. header is null or invalid.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = header.split(" ")[1].trim(); // Bearer 과 토큰 사이에 구분자 공백이 있음, 토큰만 가져옴

            // TODO : check token is valid

            // TODO : get username form token
            String userName = "";

            // TODO : check the user is valid

            // 유효한 유저임까지 확인했으면 context에 넣어서 controller로 보냄
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    // TODO
                    null, null, null
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("Error occur while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
