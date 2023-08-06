package com.eunhong.sns.service;

import com.eunhong.sns.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // TODO : implement
    public User join() {
        return new User();
    }

    // TODO : implement
    // jwt는 문자열 암호화 인가 방식, 로그인에 사용할 암호화된 문자열 반환
    // 로그인 성공 시 토큰 반환
    public String login() {
        return "";
    }
}
