package com.eunhong.sns.util;

import java.util.Optional;

public class ClassUtils {

    public static <T> Optional<T> getSafeCastInstance(Object o, Class<T> clss) {
        // 어떤 오브젝트를 어떤 클래스로 캐스팅한 결과를 Optional으로 감싸서 리턴한다.
        return clss != null && clss.isInstance(o) ? Optional.of(clss.cast(o)) : Optional.empty();
    }
}
