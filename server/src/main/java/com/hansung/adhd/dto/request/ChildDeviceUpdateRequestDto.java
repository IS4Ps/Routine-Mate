package com.hansung.adhd.dto.request;

/**
 * 아이 기기 번호 갱신 시 프론트엔드가 보내는 데이터 바구니
 */
public record ChildDeviceUpdateRequestDto(
        String newDeviceId
) {
}