package com.eunhong.sns.controller.response;

import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import com.eunhong.sns.model.User;
import com.eunhong.sns.model.UserRole;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserJoinResponse {
    private Integer id;
    private String userName;
    private UserRole role;

    public static UserJoinResponse fromUser(User user) {
        return new UserJoinResponse(
            user.getId(),
            user.getUserName(),
            user.getUserRole()
        );
    }
}
