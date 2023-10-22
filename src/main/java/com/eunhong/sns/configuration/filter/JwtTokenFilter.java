package com.eunhong.sns.configuration.filter;

import com.eunhong.sns.model.User;
import com.eunhong.sns.service.UserService;
import com.eunhong.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter { // 매 요청 때마다 필터 씌움

    private final String key;
    private final UserService userService;

    private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/api/v1/users/alarm/subscribe");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String token;

        try {

            if(TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) { // 토큰이 url에 있는 api리스트에 request의 URI가 해당하면
                // 토큰을 request의 쿼리스트링(파라미터)에서 가져온다.
                log.info("Request with {} check the query param", request.getRequestURI());
                token = request.getQueryString().split("=")[1].trim(); // "=" 옆에 있는 값으로 잘라서 가져옴
            }
            else {
                // 보통은 헤더에서 토큰을 가져와서 검증함
                final String header = request.getHeader(HttpHeaders.AUTHORIZATION); // header의 AUTHORIZATION에 토큰 값 넣어줌
                if(header == null || !header.startsWith("Bearer ")) {
                    // Bearer 토큰을 사용할 것, 토큰 헤더가 'Bearer'로 시작해야 함
                    log.error("Error occurs while getting header. header is null or invalid.");
                    filterChain.doFilter(request, response);
                    return;
                }

                token = header.split(" ")[1].trim(); // Bearer 과 토큰 사이에 구분자 공백이 있음, 토큰만 가져옴
            }

            // 토큰이 만료 됐는지 확인
            if(JwtTokenUtils.isExpired(token, key)) {
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }

            String userName = JwtTokenUtils.getUserName(token, key);

            User user = userService.loadUserByUserName(userName);

            // 유효한 유저임까지 확인했으면 context에 넣어서 controller로 보냄
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
            // authentication 객체에 userDetails 가져온 것 넣어주기
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("Error occur while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
