package com.eunhong.sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "User name is duplicated"), // 회원가입 시 중복된 유저네임
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"), // 로그인 시 해당 유저 없음
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Password is invalid"), // 로그인 시 패스워드 틀림

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    ;

    // enum 의 각 필드 (status, message)
    private HttpStatus status; // 어떤 에러인지에 따라 HttpStatus 정의
    private String message;
}
