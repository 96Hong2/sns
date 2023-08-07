package com.eunhong.sns.service;

import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.model.User;
import com.eunhong.sns.model.entity.UserEntity;
import com.eunhong.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;

    // TODO : implement
    public User join(String userName, String password) {
        // 회원가입하려는 userName으로 회원가입된 user가 있는지 체크
        Optional<UserEntity> userEntity = userEntityRepository.findByUserName(userName);

        // 회원가입 진행 = user 등록
        userEntityRepository.save(new UserEntity());

        return new User();
    }

    // TODO : implement
    // jwt는 문자열 암호화 인가 방식, 로그인에 사용할 암호화된 문자열 반환
    // 로그인 성공 시 토큰 반환
    public String login(String userName, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException());

        // 비밀번호 체크
        if(userEntity.getPassword().equals(password)){
            throw new SnsApplicationException();
        }

        // 토큰 생성

        return "";
    }
}
