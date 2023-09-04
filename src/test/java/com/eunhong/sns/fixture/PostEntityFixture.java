package com.eunhong.sns.fixture;

import com.eunhong.sns.model.entity.PostEntity;
import com.eunhong.sns.model.entity.UserEntity;

public class PostEntityFixture {
    // PostEntity를 반환해주는 메서드
    public static PostEntity get(String userName, Integer postId, Integer userId) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUserName(userName);

        PostEntity result = new PostEntity();
        result.setId(1);
        result.setUser(user);
        result.setId(postId);
        return result;
    }
}
