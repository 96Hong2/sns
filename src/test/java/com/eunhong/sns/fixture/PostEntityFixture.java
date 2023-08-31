package com.eunhong.sns.fixture;

import com.eunhong.sns.model.entity.PostEntity;
import com.eunhong.sns.model.entity.UserEntity;

public class PostEntityFixture {
    // PostEntity를 반환해주는 메서드
    public static PostEntity get(String userName, Integer postId) {
        UserEntity user = new UserEntity();
        user.setId(1);
        user.setUserName(userName);

        PostEntity result = new PostEntity();
        result.setId(1);
        result.setUser(user);
        result.setId(postId);
        return result;
    }
}
