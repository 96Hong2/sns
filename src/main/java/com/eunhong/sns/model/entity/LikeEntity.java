package com.eunhong.sns.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"like\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"like\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user ; // 어떤 유저가 좋아요 눌렀는지

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post; // 좋아요한 게시물

    // 등록 시각
    @Column(name = "registered_at")
    private Timestamp registeredAt;

    // 수정 시각
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // 삭제 시각, soft delete (실제 row삭제가 아닌 삭제 flag 변경)
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
        // 유저 엔티티가 영속되기 전에 실행되어 registeredAt을 현재시각으로 업데이트함
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
        // 유저 엔티티가 업데이트되기 전에 updatedAt을 현재시각으로 업데이트함
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    // LikeEntity를 반환하는 메서드 of 생성
    public static LikeEntity of(UserEntity userEntity, PostEntity postEntity) {
        LikeEntity entity = new LikeEntity();
        entity.setUser(userEntity);
        entity.setPost(postEntity);
        return entity;
    }
}
