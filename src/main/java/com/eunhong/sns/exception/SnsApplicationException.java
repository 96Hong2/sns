package com.eunhong.sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SnsApplicationException extends Exception{

    private ErrorCode errorCode;
    private String message;

    @Override
    public String getMessage() {
        if(message == null) { // exception의 메시지가 없으면 errorCode의 메시지만 리턴
            return errorCode.getMessage();
        }

        // 에러코드의 메시지와 실제 exception의 메시지를 붙여서 표시한 문자열 반환
        return String.format("%s, %s", errorCode.getMessage(), message);
    }
}
