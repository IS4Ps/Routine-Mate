package com.hansung.adhd.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final int code;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, int code, String message, T data) {
        this.success = success;
        this.code    = code;
        this.message = message;
        this.data    = data;
    }

    // ── 성공 ──────────────────────────────────────────────
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, 200, "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, 200, message, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, 201, "리소스가 성공적으로 생성되었습니다.", data);
    }

    public static ApiResponse<Void> noContent() {
        return new ApiResponse<>(true, 204, "처리가 완료되었습니다.", null);
    }

    // ── 실패 ──────────────────────────────────────────────
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    public static <T> ApiResponse<T> of(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
}