package com.hansung.adhd.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiResponse<T> {

    private String status;  // "SUCCESS" 또는 "ERROR"
    private String message; // 프론트엔드에 보여줄 메시지
    private T data;         // 실제 전달할 데이터

    // 데이터만 반환하는 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", "요청에 성공하였습니다.", data);
    }

    // 커스텀 메시지를 포함하는 성공 응답
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("SUCCESS", message, data);
    }

    // 에러 발생 시 데이터 없이 메시지만 반환하는 실패 응답
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("ERROR", message, null);
    }
}