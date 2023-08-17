package com.eunhong.sns.controller.response;

import com.eunhong.sns.model.User;
import com.eunhong.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public class UserLoginResponse {

    private String token;
}
