package com.eunhong.sns.controller.response;

import com.eunhong.sns.model.User;
import com.eunhong.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.config.core.userdetails.UserDetailsResourceFactoryBean;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Integer id;
    private String userName;
    private UserRole role;

    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getUserRole()
        );
    }
}
