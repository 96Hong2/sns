package com.eunhong.sns.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmArgs {
    // 어떤 항목(게시물 등)에 대하여, 어떤 유저에 의한 것인지 등 기능 확장을 위한 필드

    private Integer fromUserId; // 알람을 발생시킨 사람
    private Integer targetId; // 알람 주체 (포스트 등)
}

// comment : 00 씨가 새 코멘트를 작성했습니다. -> postId, commentId 필요
// 00 외 2명이 새 코멘트를 작성했습니다. -> commentId, commentId 필요
// 필드가 고정되어있지 않고 모두 컬럼으로 만들게 되면 알림에 따라 null 이 많이 발생한다. 따라서 arg로 유연하게 관리
