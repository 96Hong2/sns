package com.eunhong.sns.repository;

import com.eunhong.sns.model.entity.PostEntity;
import com.eunhong.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {

    // 특정 유저가 작성한 글 목록을 페이징하여 조회해오는 커스텀 메소드
    Page<PostEntity> findAllByUser(UserEntity entity, Pageable pageable);
}
