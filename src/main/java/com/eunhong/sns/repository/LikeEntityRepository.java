package com.eunhong.sns.repository;

import com.eunhong.sns.model.entity.LikeEntity;
import com.eunhong.sns.model.entity.PostEntity;
import com.eunhong.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

    // 이 유저가 이 포스트에 like한 row가 있는지 찾음
    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);
}
