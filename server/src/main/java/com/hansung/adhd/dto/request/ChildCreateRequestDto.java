package com.hansung.adhd.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChildCreateRequestDto {
    // 프론트엔드에서 회원가입(아이 낳기) 버튼 누를 때 던져줄 두 가지 정보!
    private String nickname;
    private String lastConnectedDeviceId;
}