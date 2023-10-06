package com.eunhong.sns.model.entity;

import com.eunhong.sns.model.AlarmArgs;
import com.eunhong.sns.model.AlarmType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"alarm\"", indexes = { // userId로 인덱스 걸어줌
        @Index(name = "user_id_idx", columnList = "user_id")
})
@Getter
@Setter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLDelete(sql = "UPDATE \"alarm\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user ; // 알림 받는 유저

    // 알림 타입
    // 알림 타입별로 개수를 모아서 보여줄 수 있음
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    // 어떤 항목(게시물 등)에 대하여, 어떤 유저에 의한 것인지 등 기능 확장을 위한 필드
    @Type(type = "jsonb") // postgres에서는 json과 jsonb 타입을 지원, jsonb만 인덱스를 걸 수 있다.
    // jsonb 타입을 사용하려면 build.gradle에 com.vladmihalcea:hibernate-types-52:2.17.3 오픈소스 추가 필요
    @Column(columnDefinition = "json") // 추후 변경 가능성이 높아 JSON 타입으로 생성
    private AlarmArgs args;

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
        // 엔티티가 영속되기 전에 실행되어 registeredAt을 현재시각으로 업데이트함
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
        // 엔티티가 업데이트되기 전에 updatedAt을 현재시각으로 업데이트함
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    // Entity를 반환하는 메서드 of 생성
    public static AlarmEntity of(UserEntity userEntity, AlarmType alarmType, AlarmArgs args) {
        AlarmEntity entity = new AlarmEntity();
        entity.setUser(userEntity);
        entity.setAlarmType(alarmType);
        entity.setArgs(args);
        return entity;
    }
}
