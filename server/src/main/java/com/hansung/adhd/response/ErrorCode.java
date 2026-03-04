package com.hansung.adhd.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ── 공통 ──────────────────────────────────────────────
    BAD_REQUEST(400,       "잘못된 요청입니다."),
    UNAUTHORIZED(401,      "인증이 필요합니다."),
    FORBIDDEN(403,         "접근 권한이 없습니다."),
    NOT_FOUND(404,         "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405,"허용되지 않는 HTTP 메서드입니다."),
    CONFLICT(409,          "이미 존재하는 리소스입니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

    // ── 인증 (AUTH) ───────────────────────────────────────
    INVALID_TOKEN(401,           "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401,           "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(401,       "지원하지 않는 토큰 형식입니다."),
    TOKEN_NOT_FOUND(401,         "토큰이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(401,   "유효하지 않은 리프레시 토큰입니다."),
    INVALID_OAUTH_PROVIDER(400,  "지원하지 않는 OAuth 공급자입니다."),

    // ── 부모 (PARENTS) ────────────────────────────────────
    PARENT_NOT_FOUND(404,        "부모 계정을 찾을 수 없습니다."),
    PARENT_ALREADY_EXISTS(409,   "이미 가입된 이메일입니다."),

    // ── 아이 (CHILDREN) ───────────────────────────────────
    CHILD_NOT_FOUND(404,         "아이 계정을 찾을 수 없습니다."),
    CHILD_ALREADY_DELETED(400,   "이미 삭제된 아이 계정입니다."),
    INVALID_DEVICE_ID(401,       "등록되지 않은 디바이스입니다."),
    LEVEL_MAX_REACHED(400,       "이미 최대 레벨입니다."),

    // ── 관계 (FAMILY RELATIONS) ───────────────────────────
    RELATION_NOT_FOUND(404,      "가족 관계를 찾을 수 없습니다."),
    RELATION_ALREADY_EXISTS(409, "이미 연결된 아이입니다."),
    NOT_YOUR_CHILD(403,          "해당 아이에 대한 접근 권한이 없습니다."),

    // ── 미션 (MISSION) ────────────────────────────────────
    MISSION_NOT_FOUND(404,       "미션을 찾을 수 없습니다."),
    MISSION_ALREADY_COMPLETED(400, "이미 완료된 미션입니다."),
    MISSION_ALREADY_APPROVED(400,  "이미 승인 처리된 미션입니다."),
    MISSION_NOT_PENDING(400,     "승인 대기 상태의 미션이 아닙니다."),
    PRESET_NOT_FOUND(404,        "프리셋을 찾을 수 없습니다."),

    // ── 아이템 / 인벤토리 ─────────────────────────────────
    ITEM_NOT_FOUND(404,          "아이템을 찾을 수 없습니다."),
    INSUFFICIENT_GOLD(400,       "골드가 부족합니다."),
    ITEM_ALREADY_OWNED(409,      "이미 보유 중인 아이템입니다."),
    INVENTORY_NOT_FOUND(404,     "인벤토리 항목을 찾을 수 없습니다."),
    ITEM_LEVEL_REQUIRED(400,     "아이템 구매 가능 레벨이 부족합니다."),
    ITEM_JOB_MISMATCH(400,       "해당 직업으로 구매할 수 없는 아이템입니다."),

    // ── 보상 (REWARD) ─────────────────────────────────────
    REWARD_NOT_FOUND(404,        "보상을 찾을 수 없습니다."),
    REWARD_ALREADY_CLAIMED(400,  "이미 수령한 보상입니다."),

    // ── 감정 (MOOD) ───────────────────────────────────────
    MOOD_NOT_FOUND(404,          "감정 기록을 찾을 수 없습니다."),
    MOOD_ALREADY_RECORDED(409,   "오늘은 이미 감정을 기록했습니다."),

    // ── AI ────────────────────────────────────────────────
    AI_REQUEST_FAILED(500,       "AI 요청 처리에 실패했습니다."),
    QUIZ_NOT_FOUND(404,          "퀴즈를 찾을 수 없습니다."),
    QUIZ_ALREADY_SOLVED(400,     "이미 풀었던 퀴즈입니다.");

    private final int    code;
    private final String message;
}