package com.eunhong.sns.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserJoinRequest {
    private String name;
    private String password;
}
