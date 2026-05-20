package com.hansung.adhd.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 아이 기기 번호 갱신 시 프론트엔드가 보내는 데이터 바구니
 */
public record ChildDeviceUpdateRequestDto(
        @NotBlank(message = "새로운 기기 ID는 필수입니다.")
        String newDeviceId
) {
}