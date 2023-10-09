package com.eunhong.sns.service;

import com.eunhong.sns.exception.ErrorCode;
import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.model.Alarm;
import com.eunhong.sns.model.User;
import com.eunhong.sns.model.entity.UserEntity;
import com.eunhong.sns.repository.AlarmEntityRepository;
import com.eunhong.sns.repository.UserEntityRepository;
import com.eunhong.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final BCryptPasswordEncoder encoder;

    // configuration(application.yaml)에서 값을 받아옴
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUserName(String userName) {
        // findByUserName으로 찾은 UserEntity객체를 User 객체로 반환해야 함
        // User클래스의 메서드 fromEntity의 파라미터로 적용하기 위해 map(User::fromEntity) 사용
        return userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded.", userName)));
    }

    @Transactional
    public User join(String userName, String password) {
        // 회원가입하려는 userName으로 회원가입된 user가 있는지 체크
        // 만약 user가 있다면 에러 throw
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원가입 진행 = user 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    // jwt는 문자열 암호화 인가 방식, 로그인에 사용할 암호화된 문자열 반환
    // 로그인 성공 시 토큰 반환
    public String login(String userName, String password) throws SnsApplicationException { // 이거 throws부분은 추가함
        // 회원가입 여부 체크 (가입된 유저가 없다면, Throw Error)
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 비밀번호 체크
        if(!encoder.matches(password, userEntity.getPassword())){
        // if(userEntity.getPassword().equals(password)){
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);

        return token;
    }

    public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
        // user exist
        // UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        return alarmEntityRepository.findAllByUserId(userId, pageable).map(Alarm::fromEntity);
    }
}
