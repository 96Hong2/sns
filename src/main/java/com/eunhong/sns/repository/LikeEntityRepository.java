package com.eunhong.sns.repository;

import com.eunhong.sns.model.entity.LikeEntity;
import com.eunhong.sns.model.entity.PostEntity;
import com.eunhong.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

    // 이 유저가 이 포스트에 like한 row가 있는지 찾음
    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    // 해당 게시물에 존재하는 likeEntity를 모두 가져옴
    // List<LikeEntity> findAllByPost(PostEntity post);

    // 해당 게시물에 존재하는 like의 개수를 가져옴
    // 쿼리 사용 : like 테이블을 LikeEntity로 사용하고 있으므로 그 객체를 from에 넣고,
    // 해당 entity의 post가 파라미터로 받은 post와 같은 것의 개수를 반환함
    // @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post =:post")
    // Integer countByPost(@Param("post") PostEntity post);

    // 위 쿼리 간단하게 작성 : JPA에서 CountBy000 에 대한 쿼리 자동으로 생성해주는 기능 이용
    long countByPost(PostEntity post);

}
