package com.eunhong.sns.model.entity;

import com.eunhong.sns.model.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
// postgreSQL에 별도의 user테이블이 존재하기 때문에 우리가 만든 테이블 사용하기 위해 user에 따옴표 붙여줘야 함
@Table(name = "\"user\"")
@Getter
@Setter
// delete 요청이 들어왔을 때 아래 sql을 실행한다.
@SQLDelete(sql = "UPDATE \"user\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    // 유저 권한
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

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

    // 새 UserEntity를 만들어주는 메서드
    public static UserEntity of(String userName, String password)
    {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        userEntity.setPassword(password);
        return userEntity;
    }
}
