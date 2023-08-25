package com.eunhong.sns.model.entity;

import com.eunhong.sns.model.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"post\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATED \"post\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    // String은 본문에 사용하기 작아서 TEXT 타입으로 바꿔줌
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    // 이 포스트를 작성한 유저 확인을 위해 PostEntity안에 UserEntity 저장
    // 연관관계 설정
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user ;

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

    // PostEntity를 반환하는 메서드 of 생성
    public static PostEntity of(String title, String body, UserEntity userEntity) {
        PostEntity entity = new PostEntity();
        entity.setTitle(title);
        entity.setBody(body);
        entity.setUser(userEntity);
        return entity;
    }
}
